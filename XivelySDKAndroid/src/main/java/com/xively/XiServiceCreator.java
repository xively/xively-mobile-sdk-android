package com.xively;

import java.util.ArrayList;

public abstract class XiServiceCreator<T extends XiService> {

    public enum State {Idle, Creating, Created, Error, Canceled}

    protected State state = State.Idle;
    protected final ArrayList<XiServiceCreatorCallback<T>> creatorListeners
            = new ArrayList<>();

    protected final Object cancelSync = new Object();

    /**
     * Returns the current state of this creator.
     *
     * @return A {@link com.xively.XiServiceCreator.State} value.
     */
    public State getState(){
        return state;
    }

    /**
     * Cancels the creation of the service.
     * After cancel has been invoked the Creator Callbacks will never be invoked and
     * the service eventually being created is always discarded.
     *
     * If the Creator has already reached a terminal state (Created, Error or Canceled),
     * the invocation of cancel has no effect whatsoever.
     */
    public void cancel(){
        synchronized (cancelSync){
            if (state == State.Idle || state == State.Creating) {
                state = State.Canceled;
            }
        }
    }

    /**
     * Adds a new service creator listener. These provide asynchronous callbacks
     * for service creation.
     * If a listener is already added, no action is taken.
     *
     * @param callback An {@link XiServiceCreatorCallback} instance.
     *                 Each service creator instance expects to specify its matching service type
     *                 in this parameter.
     */
    public void addServiceCreatorCallback(XiServiceCreatorCallback<T> callback){
        if (! creatorListeners.contains(callback)){
            creatorListeners.add(callback);
        }
    }

    /**
     * Removes a Service Creator Callback listener. If the listener instance is already removed
     * or has never been added, no action is taken.
     *
     * @param callback An {@link XiServiceCreatorCallback} instance.
     */
    public void removeServiceCreatorCallback(XiServiceCreatorCallback<T> callback){
        creatorListeners.remove(callback);
    }

    /**
     *
     * Clears all callback listeners registered to this creator instance.
     */
    public void removeAllCallbacks(){
        creatorListeners.clear();
    }

    /**
     * Fire success event on all listeners.
     *
     * @param service The service instance to be passed to clients.
     */
    protected void serviceCreated(T service){
        synchronized (cancelSync) {
            if (state != State.Canceled) {
                this.state = State.Created;
                for (XiServiceCreatorCallback<T> listener : creatorListeners) {
                    listener.onServiceCreated(service);
                }
            }
        }
    }

    /**
     * Fire failed event on all listeners.
     */
    protected void serviceCreateFailed(){
        synchronized (cancelSync) {
            if (state != State.Canceled) {
                this.state = State.Error;
                for (XiServiceCreatorCallback<T> listener : creatorListeners) {
                    listener.onServiceCreateFailed();
                }
            }
        }
    }

}
