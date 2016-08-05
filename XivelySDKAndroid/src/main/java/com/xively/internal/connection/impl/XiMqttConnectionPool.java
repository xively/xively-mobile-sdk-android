package com.xively.internal.connection.impl;

import com.xively.XiException;
import com.xively.XiService;
import com.xively.internal.connection.ConnectionListener;
import com.xively.internal.connection.XiMqttConnection;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiLastWill;

import java.util.ArrayList;

/**
 * @Internal
 */
public class XiMqttConnectionPool {

    private static final String TAG = "XiMqttConnectionPool";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private XiMqttConnection mqttConnection = null;
    private XivelyAccount xivelyAccount;

    private ArrayList<XiService> connectionUsers;

    public XiMqttConnectionPool(XivelyAccount xivelyAccount){
        connectionUsers = new ArrayList<>(5);
        this.xivelyAccount = xivelyAccount;
    }

    //Only for unit testing
    protected void setTestConnectionObject(XiMqttConnection conn){
        this.mqttConnection = conn;
    }

    public void requestConnection(final XiService service,
                                  String jwt,
                                  boolean cleanSession,
                                  XiLastWill lastWill,
                                  final MqttConnectionRequestCallback callback)
            throws IllegalArgumentException {

        log.t("Connection request: " + service.getClass().getName());

        if (mqttConnection != null) {
            //Check if the cleanSession and lastWill params are the same
            if (( cleanSession != mqttConnection.isCleanSession()) ||              //Clean session param mismatch
                    ((lastWill == null) ^ (mqttConnection.getLastWill() == null)) ||//one has last will the other not
                    (lastWill != null && mqttConnection.getLastWill() != null && //both have last wills but they are not equal
                        !lastWill.equals(mqttConnection.getLastWill()))) {
                throw new IllegalArgumentException();
            }
        }

        if (mqttConnection == null || !mqttConnection.isConnected()){
            log.d("Creating new mqtt connection.");
            mqttConnection = new XiMqttConnectionImpl();

            ConnectionListener connectionListener = new ConnectionListener() {
                @Override
                public void onConnected() {
                    mqttConnection.removeConnectionListener(this);
                    if (!connectionUsers.contains(service)) {
                        connectionUsers.add(service);
                    }
                    callback.onConnectionReady(mqttConnection);
                }

                @Override
                public void onReconnected() {}

                @Override
                public void onReconnecting() {}

                @Override
                public void onDisconnected() {}

                @Override
                public void onClosed() {
                    mqttConnection.removeConnectionListener(this);
                    callback.onConnectFailed(ConnectionError.CONNECTION_LOST);
                }

                @Override
                public void onError(ConnectionError error) {
                    mqttConnection.removeConnectionListener(this);
                    callback.onConnectFailed(error);
                }
            };

            mqttConnection.addConnectionListener(connectionListener);
            mqttConnection.connect(xivelyAccount, jwt, cleanSession, lastWill);
        } else {
            if (!connectionUsers.contains(service)) {
                connectionUsers.add(service);
            }
            callback.onConnectionReady(mqttConnection);
        }

    }

    public void releaseConnection(XiService service){

        log.t("Connection release request: " + service.getClass().getName());

        connectionUsers.remove(service);
        if (connectionUsers.size() == 0){
            log.d("No more connection users, shutting down mqtt.");
            closeMqttConnection();
        }
    }

    public void dropConnection(){
        closeMqttConnection();
    }

    private void closeMqttConnection(){
        if (mqttConnection != null &&
                mqttConnection.isConnected()){
            mqttConnection.disconnect();
            mqttConnection = null;
        }
    }

    public interface MqttConnectionRequestCallback{
        void onConnectionReady(XiMqttConnection xiMqttConnection);
        void onConnectFailed(ConnectionListener.ConnectionError error);
    }

}
