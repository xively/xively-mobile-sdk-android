package com.xively.internal.connection.impl;

import android.content.Context;

import com.xively.internal.connection.ConnectionListener;
import com.xively.internal.connection.ConnectionListener.ConnectionError;
import com.xively.internal.connection.PublishListener;
import com.xively.internal.connection.XiMqttConnection;
import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.util.AsyncTimerTask;
import com.xively.messaging.XiLastWill;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.SSLContext;

public class XiMqttConnectionImpl implements XiMqttConnection {
	private static final String TAG = "XMqttConnection";
    private static final LMILog log = new LMILog(TAG);
	static {
		log.getClass();
	}

    private final static Object CONNECT_SYNC = new Object();

    private boolean cleanMqttSession = true;
    private XiLastWill lastWill = null;
    private int reconnectCount = 0;
    private boolean connCanceled = false;
    private AsyncTimerTask mqttConnectionTimer;

    private final CopyOnWriteArrayList<ConnectionListener> connectionListeners;
	private final CopyOnWriteArrayList<PublishListener> publishListeners;
	private MqttAndroidClient client;
    private String currentJwt = null;
    private String currentClientId = null;
    private String currentUserId = null;

	public XiMqttConnectionImpl() {
		connectionListeners = new CopyOnWriteArrayList<>();
		publishListeners = new CopyOnWriteArrayList<>();
	}

    public boolean isCleanSession() {
        return this.cleanMqttSession;
    }


    public XiLastWill getLastWill() {
        return this.lastWill;
    }

	public void connect(XivelyAccount xivelyAccount, String token, boolean cleanSession, XiLastWill lastWill) {
        this.currentClientId = xivelyAccount.getClientId();
        this.currentUserId = xivelyAccount.getUserName();
        this.currentJwt = token;
        this.cleanMqttSession = cleanSession;
        this.lastWill = lastWill;

        connectMqtt(xivelyAccount);
	}

    private void connectMqtt(final XivelyAccount xivelyAccount) {
        connectMqtt(xivelyAccount, false);
    }

	private void connectMqtt(final XivelyAccount xivelyAccount, final boolean reconnect) {
		log.d("Connecting to mqtt messaging...");
        connCanceled = false;

		Context context = DependencyInjector.get().getContext();

		String uri;
		if (Config.CONN_USE_SSL) {
			uri = "ssl://";
            uri = uri + Config.xi_mqtt_host() + ":"
                    + Config.CONN_XI_MQTT_SECURE_PORT;
		} else {
			uri = "tcp://";
            uri = uri + Config.xi_mqtt_host() + ":"
                    + Config.CONN_XI_MQTT_PORT;
		}

        log.t("Connecting to " + uri);

        if (!reconnect) {
            client = DependencyInjector.get().createMqttAndroidClient(context, uri, xivelyAccount.getUserName());
            client.registerResources(context);

            MqttCallback callback = new MqttCallback() {

                @Override
                public void messageArrived(String arg0, MqttMessage arg1)
                        throws Exception {
                    onMessageReceived(arg0, arg1);
                    log.d("New message received.");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken arg0) {
                    try {
                        if (arg0.getMessage().getQos() >= 1) {
                            onMessageConfirmation(arg0.getMessageId());
                        }
                    } catch (MqttException e) {
                        log.w("Message delivery confirmation error: " + e);
                    }
                }

                @Override
                public void connectionLost(Throwable arg0) {
                    log.w("Connection lost: " + arg0);

                    if (reconnectCount >= Config.CONN_MQTT_MAX_RECONNECT) {
                        log.w("Disconnecting...");
                        onDisconnected();
                        onError(ConnectionError.CONNECTION_LOST);
                    } else {
                        //try to reconnect
                        onReconnecting();
                        reconnectCount ++;
                        log.w("Reconnecting... (" + reconnectCount + ")");
                        try {
                            Thread.sleep(Config.CONN_MQTT_RECONNECT_DELAY);
                        } catch (InterruptedException e) {
                            log.d("Reconnect delay interrupted.");
                        }
                        connectMqtt(xivelyAccount, true);
                    }
                }
            };
            client.setCallback(callback);

            try {
                mqttConnectionTimer = DependencyInjector.get()
                        .createAsyncTimerTask(Config.CONN_MQTT_MAX_TIMEOUT,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        log.e("Connection timed out.");
                                        cancelConnection(ConnectionError.TIMED_OUT);
                                    }
                                });
                mqttConnectionTimer.execute();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
        }

		// TODO: check ssl cert - client.getSSLSocketFactory can return the key
        MqttConnectOptions options = new MqttConnectOptions();

		options.setUserName("Auth:JWT");
		options.setPassword(currentJwt.toCharArray());

		options.setCleanSession(cleanMqttSession);
        if (this.getLastWill() != null) {
            options.setWill(this.lastWill.getTopic(),
                    this.getLastWill().getMessage(),
                    this.getLastWill().getQosNumber(),
                    this.getLastWill().getRetain());
        }

        options.setConnectionTimeout(Config.CONN_MQTT_MAX_TIMEOUT);
		options.setKeepAliveInterval(Config.CONN_KEEPALIVE);

        if (Config.CONN_USE_SSL){
            try {
                SSLContext sslContext = SSLContext.getDefault();//SSLContext.getInstance("TLSv1");
                options.setSocketFactory(sslContext.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                log.e("Failed to create ssl context.");
            }

        }

		try {
			IMqttActionListener actionCallback = new IMqttActionListener() {

				@Override
				public void onSuccess(IMqttToken arg0) {
                    if (connCanceled){
                        return;
                    }

                    synchronized (CONNECT_SYNC){
                        if (mqttConnectionTimer != null) {
                            mqttConnectionTimer.cancel();
                        }
                    }

                    reconnectCount = 0;
                    if (reconnect) {
                        onReconnected();
                        log.d("Reconnected.");
                    } else {
                        onConnected();
                        log.d("Connected.");
                    }
				}

				@Override
				public void onFailure(IMqttToken arg0, Throwable arg1) {
                    if (connCanceled){
                        return;
                    }

                    if (reconnectCount >= Config.CONN_MQTT_MAX_RECONNECT) {
                        log.e("Failed to connect: " + arg1);
                        mqttConnectionTimer.cancel();
                        onError(ConnectionError.FAILED_TO_CONNECT);
                    } else {
                        //try to reconnect
                        reconnectCount ++;
                        log.w("Reconnecting... (" + reconnectCount + ")");
                        try {
                            Thread.sleep(Config.CONN_MQTT_RECONNECT_DELAY);
                        } catch (InterruptedException e) {
                            log.d("Reconnect delay interrupted.");
                        }
                        if (mqttConnectionTimer != null) {
                            connectMqtt(xivelyAccount, true);
                        }
                    }
				}
			};

			client.connect(options, context, actionCallback);

		} catch (MqttException e) {
            if (reconnectCount >= Config.CONN_MQTT_MAX_RECONNECT) {
                log.e("Failed to connect: " + e);
                cancelConnection(ConnectionError.FAILED_TO_CONNECT);
            } else {
                //try to reconnect
                reconnectCount ++;
                log.w("Reconnecting... (" + reconnectCount + ")");
                try {
                    Thread.sleep(Config.CONN_MQTT_RECONNECT_DELAY);
                } catch (InterruptedException ex) {
                    log.d("Reconnect delay interrupted.");
                }
                connectMqtt(xivelyAccount, true);
            }
		}

	}

	public boolean isConnected(){
        try {
            return client != null && client.isConnected();
        } catch (Exception ex){
            log.d("Client isConnected failed: " + ex);
        }

        return false;
    }

    @Override
    public String getClientId() {
        return currentClientId;
    }

    @Override
    public String getUserName(){
        return currentUserId;
    }


    public void disconnect() {
		if (client != null && client.isConnected()) {
			try {
                client.setCallback(null);//we don't need any more message delivery
                onDisconnected();

                client.disconnect(Config.CONN_MQTT_DISCONNECT_TIMEOUT,
                        DependencyInjector.get().getContext(),
                        new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken iMqttToken) {
                                log.d("Mqtt disconnected.");
                                onClosed();
                                client.unregisterResources();
                            }

                            @Override
                            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                                log.d("Mqtt disconnect failure: " + throwable);
                                onClosed();
                                client.unregisterResources();
                            }
                        });
			} catch (MqttException e) {
				log.e("Disconnect error: " + e);
                client.unregisterResources();
				client = null;
				onError(ConnectionError.DISCONNECT_ERROR);
			} catch (IllegalArgumentException e){
                log.e("Disconnect error: " + e);
                onClosed();
                client.unregisterResources();
                client = null;
            }
		} else {
            onClosed();
        }
	}

    public void cancelConnection(ConnectionError reason){
        connCanceled = true;

        if (mqttConnectionTimer != null){
            mqttConnectionTimer.cancel();
        }

        if (client != null){
            client.setCallback(null);//we don't need any more message delivery
            if (client.isConnected()){
                try {
                    client.disconnect(Config.CONN_MQTT_DISCONNECT_TIMEOUT);
                } catch (Exception ex){
                    log.d("Disconnect warning: " + ex);
                } finally {
                    client.unregisterResources();
                    client = null;
                }
            }
        }

        if (reason != null){
            onError(reason);
        } else {
            onError(ConnectionError.FAILED_TO_CONNECT);
        }
    }

	public int publish(String message, String topic, int qos) {
		return publish(message.getBytes(), topic, qos, false);
	}

    public int publish(byte[] message, String topic, int qos, boolean retained) {
        try {
            int res = client.publish(topic, message, qos, retained).getMessageId();
            if (qos < 1){
                res = 0;
            }
            log.t("Message sent: " + res);
            return res;
        } catch (MqttException e) {
            log.e("Publish error: " + e);
            return -1;
        }
    }

    public int publish(String message, String topic) {
        return publish(message, topic, Config.CONN_XI_MQTT_QOS);
    }

	public void addConnectionListener(ConnectionListener listener) {
        if (listener != null && !connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
	}

	public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
	}

	public void addPublishListener(PublishListener listener) {
        if (listener != null && !publishListeners.contains(listener)) {
            publishListeners.add(listener);
        }
	}

	public void removePublishListener(PublishListener listener) {
        publishListeners.remove(listener);
	}

    @Override
	public void subscribeToTopic(String topic) throws MqttException {
        subscribeToTopic(topic, Config.CONN_XI_MQTT_QOS);
	}

    @Override
    public void subscribeToTopic(String topic, int qos) throws MqttException {
        try {
            client.subscribe(topic, qos);
            log.d("Topic subscription success.");
        } catch (MqttException e) {
            log.e("Error while subscribing to channel: " + e);
            throw(e);
        }
    }

    @Override
	public void unsubscribeFromTopic(String topic) throws MqttException  {
		try {
			client.unsubscribe(topic);
			log.d("Unsubscribed from current channel.");
		} catch (MqttException e) {
			log.e("Unsubscribe error: " + e);
            throw(e);
		}
	}

	// ===== Listener callbacks
	private void onConnected() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnected();
        }
	}

    private void onReconnecting() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onReconnecting();
        }
    }

    private void onReconnected() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onReconnected();
        }
    }

	private void onDisconnected() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onDisconnected();
        }
	}

	private void onClosed() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onDisconnected();
        }
	}

	private void onError(ConnectionListener.ConnectionError error) {
        for (ConnectionListener listener : connectionListeners) {
            listener.onError(error);
        }
	}

	private void onMessageReceived(String topic, MqttMessage message) {
		String messagePayload;
		messagePayload = new String(message.getPayload());
        for (PublishListener listener : publishListeners) {
            listener.onPublishReceived(messagePayload, topic);
        }
	}

    private void onMessageConfirmation(int messageId){
        log.d("Message delivered: " + messageId);
        for (PublishListener listener : publishListeners) {
            listener.onMessageDeliveryConfirmation(messageId);
        }
    }

}
