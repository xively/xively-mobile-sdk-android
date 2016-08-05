package com.xively.messaging;

/**
 * State change listener interface for {@link XiMessaging}.
 */
public interface XiMessagingStateListener {

    /**
     * Event for state changes of the Xively Messaging service.
     *
     * @param newState The new state.
     */
    void onStateChanged(XiMessaging.State newState);

    /**
     * Event for unrecoverable errors of the Xively Messaging Service.
     * On this event the service will be in an error state and cannot be restarted any more.
     * A new messaging instance should be created and started.
     */
    void onError();
}
