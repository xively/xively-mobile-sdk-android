package com.xively.internal.messaging;

import com.xively.XiException;
import com.xively.internal.connection.ConnectionListener;
import com.xively.internal.connection.PublishListener;
import com.xively.internal.connection.XiMqttConnection;
import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingDataListener;
import com.xively.messaging.XiMessagingStateListener;
import com.xively.messaging.XiMessagingSubscriptionListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.CopyOnWriteArrayList;

public class XiMessagingImpl implements XiMessaging, ConnectionListener, PublishListener {

    private static final String TAG = "XiMessaging";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private String jwt;
    private State state = State.Closed;
    private XiMqttConnection connection;
    private XiMqttConnectionPool connectionPool;

    final CopyOnWriteArrayList<XiMessagingDataListener> dataListeners;
    final CopyOnWriteArrayList<XiMessagingStateListener> stateListeners;
    final CopyOnWriteArrayList<XiMessagingSubscriptionListener> subscriptionListeners;

    public XiMessagingImpl(XiMqttConnectionPool connectionPool, String token){
        log.i("Messaging init.");
        jwt = token;
        dataListeners = new CopyOnWriteArrayList<>();
        stateListeners = new CopyOnWriteArrayList<>();
        subscriptionListeners = new CopyOnWriteArrayList<>();
        this.connectionPool = connectionPool;
    }

    public void init(boolean cleanSession, XiLastWill lastWill)
            throws IllegalArgumentException {
        final ConnectionListener connectionListener = this;
        final PublishListener publishListener = this;
        connectionPool.requestConnection(this, jwt, cleanSession, lastWill,
                new XiMqttConnectionPool.MqttConnectionRequestCallback() {
                    @Override
                    public void onConnectionReady(XiMqttConnection xiMqttConnection) {
                        connection = xiMqttConnection;
                        connection.addConnectionListener(connectionListener);
                        connection.addPublishListener(publishListener);
                        fireStateChanged(State.Running);
                        log.i("Messaging connected.");
                    }

                    @Override
                    public void onConnectFailed(ConnectionListener.ConnectionError error) {
                        fireStateChanged(State.Error);
                        log.e("Messaging failed to connect: " + error);
                    }
                });
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void addDataListener(XiMessagingDataListener listener) {
        synchronized (dataListeners) {
            if (listener != null && !dataListeners.contains(listener)) {
                dataListeners.add(listener);
            }
        }
    }

    @Override
    public void removeDataListener(XiMessagingDataListener listener) {
        synchronized (dataListeners){
            dataListeners.remove(listener);
        }
    }

    @Override
    public void addStateListener(XiMessagingStateListener listener) {
        synchronized (stateListeners){
            if (listener != null && !stateListeners.contains(listener)) {
                stateListeners.add(listener);
            }
        }
    }

    @Override
    public void removeStateListener(XiMessagingStateListener listener) {
        synchronized (stateListeners){
            stateListeners.remove(listener);
        }
    }

    @Override
    public void addSubscriptionListener(XiMessagingSubscriptionListener listener) {
        synchronized (subscriptionListeners){
            if (listener != null && !subscriptionListeners.contains(listener)) {
                subscriptionListeners.add(listener);
            }
        }
    }

    @Override
    public void removeSubscriptionListener(XiMessagingSubscriptionListener listener) {
        synchronized (subscriptionListeners){
            subscriptionListeners.remove(listener);
        }
    }

    @Override
    public int publish(String channel, byte[] message, XiMessagingQoS qos) throws XiException.NotConnectedException {
        if (connection == null ||
                !connection.isConnected()){
            throw new XiException.NotConnectedException();
        }
        return connection.publish(message, channel, getQosValue(qos), false);
    }

    @Override
    public int publish(String channel, byte[] message, XiMessagingQoS qos, boolean retain) throws XiException.NotConnectedException {
        if (connection == null ||
                !connection.isConnected()){
            throw new XiException.NotConnectedException();
        }
        return connection.publish(message, channel, getQosValue(qos), retain);
    }

    @Override
    public void subscribe(String channel, XiMessagingQoS qos)
            throws XiException.NotConnectedException, XiException.ConnectionException {
        if (connection == null ||
                !connection.isConnected()){
            fireSubscribeFailed(channel);
            throw new XiException.NotConnectedException();
        }
        try {
            connection.subscribeToTopic(channel, getQosValue(qos));
            fireSubscribed(channel);
        } catch (MqttException e) {
            log.w("Failed to subscribe to messaging channel.");
            log.t("Failed to subscribe to channel: " + channel);
            fireSubscribeFailed(channel);
            throw new XiException.ConnectionException();
        }
    }

    @Override
    public void unsubscribe(String channel)
            throws XiException.NotConnectedException, XiException.ConnectionException {
        if (connection == null ||
                !connection.isConnected()){
            fireUnsubscribeFailed(channel);
            throw new XiException.NotConnectedException();
        }
        try {
            connection.unsubscribeFromTopic(channel);
            fireUnsubscribed(channel);

        } catch (MqttException e) {
            log.w("Failed to unsubscribe from messaging channel.");
            log.t("Failed to unsubscribe from channel: " + channel);
            fireUnsubscribeFailed(channel);
            throw new XiException.ConnectionException();
        }
    }

    @Override
    public void close() {
        log.i("Messaging close.");

        connection.removeConnectionListener(this);
        connection.removePublishListener(this);
        connectionPool.releaseConnection(this);
        fireStateChanged(State.Closed);
    }

    //Mqtt connection state
    @Override
    public void onConnected() {
        fireStateChanged(State.Running);
    }

    @Override
    public void onReconnected() {
        fireStateChanged(State.Running);
    }

    @Override
    public void onReconnecting() {
        fireStateChanged(State.Reconnecting);
    }

    @Override
    public void onDisconnected() {
        fireStateChanged(State.Closed);
    }

    @Override
    public void onClosed() {
        fireStateChanged(State.Closed);
    }

    @Override
    public void onError(ConnectionError error) {
        fireStateChanged(State.Error);
        fireConnectionError();
    }
    //Mqtt connection state

    //Mqtt messages
    @Override
    public void onPublishReceived(String publish, String topic) {
        fireMessageReceived(publish.getBytes(), topic);
    }

    @Override
    public void onMessageDeliveryConfirmation(int messageId) {
        log.d("(MQTT) Messaging delivered. Id: " + messageId);
        fireMessageDelivered(messageId);
    }
    //Mqtt messages


    private void fireStateChanged(State state){
        if (state == this.state){
            return;
        }

        this.state = state;
        log.d("Messaging service state change: " + state);

        for (XiMessagingStateListener listener: stateListeners){
            if (listener != null){
                listener.onStateChanged(state);
            }
        }
    }

    private void fireConnectionError(){
        for (XiMessagingStateListener listener: stateListeners){
            if (listener != null){
                listener.onError();
            }
        }
    }

    private void fireMessageReceived(byte[] data, String channel){
        for (XiMessagingDataListener listener: dataListeners){
            if (listener != null){
                listener.onDataReceived(data, channel);
            }
        }
    }

    private void fireMessageDelivered(int messageId){
        for (XiMessagingDataListener listener: dataListeners){
            if (listener != null){
                listener.onDataSent(messageId);
            }
        }
    }

    private void fireSubscribed(String channel){
        for (XiMessagingSubscriptionListener listener: subscriptionListeners){
            if (listener != null){
                listener.onSubscribed(channel);
            }
        }
    }

    private void fireSubscribeFailed(String channel){
        for (XiMessagingSubscriptionListener listener: subscriptionListeners){
            if (listener != null){
                listener.onSubscribeFailed(channel);
            }
        }
    }

    private void fireUnsubscribed(String channel){
        for (XiMessagingSubscriptionListener listener: subscriptionListeners){
            if (listener != null){
                listener.onUnsubscribed(channel);
            }
        }
    }

    private void fireUnsubscribeFailed(String channel){
        for (XiMessagingSubscriptionListener listener: subscriptionListeners){
            if (listener != null){
                listener.onUnsubscribeFailed(channel);
            }
        }
    }

    private int getQosValue(XiMessagingQoS xiQos){
        switch (xiQos){

            case AtLeastOnce:
                return 0;
            case AtMostOnce:
                return 1;
            case ExactlyOnce:
                return 2;
            default:
                return 0;
        }
    }
}
