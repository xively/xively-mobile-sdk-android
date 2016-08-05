package com.xively.internal.connection;

import com.xively.internal.Config;
import com.xively.internal.account.XivelyAccount;
import com.xively.messaging.XiLastWill;

import org.eclipse.paho.client.mqttv3.MqttException;

//TODO: once authentication is defined and implemented, this class and its services should be internal

/**
 * A Xively Messaging MQTT connection and corresponding services.
 */
public interface XiMqttConnection {

    /**
     * Connects to the Xively Messaging with the credentials specified by the {@param xivelyAccount} parameter.
     *
     * @param xivelyAccount Xively end user credentials.
     * @param cleanSession Clean session.
     * @param lastWill Last will data.
     */
	public void connect(XivelyAccount xivelyAccount, String jwt, boolean cleanSession, XiLastWill lastWill);

    /**
     * Gets what clean session parameter the connection was initiated.*
     */
    public boolean isCleanSession();

    /**
     * Gets what last will parameter the connection was initiated.*
     */
    public XiLastWill getLastWill();


    /**
     * Connection status.
     *
     * @return True if the connection is alive, false if there is no connection to Xively Messaging.
     */
	public boolean isConnected();

    /**
     * Initiates disconnect. This call is asynchronous, <code>onDisconnected</code>, <code>onClosed</code>
     * <code>onError</code> events are raised when disconnect has finished.
     */
	public void disconnect();

    /**
     * The Xively client id associated with this connection.
     *
     * @return A unique string id.
     */
    public String getClientId();

    /**
     * The Xively end user id associated with this connection.
     *
     * @return A unique string id.
     */
    public String getUserName();

    /**
     * Publishes the plain text {@param message} to the specified {@param topic} as an MQTT payload.
     * The message will have QoS 1.
     *
     * @param message a plain text message with optional Markdown rich text formatting.
     * @param topic An MQTT topic / Xively Messaging channel.
     * @return Returns the message id. This id identifies the message in the delivery confirmation callback.
     * The value is -1 on message send error.
     */
	public int publish(String message, String topic);

    /**
     * Publishes the plain text {@param message} to the specified {@param topic} as an MQTT payload.
     * The message can have QoS specified by @param qos.
     *
     * @param message a plain text message with optional Markdown rich text formatting.
     * @param topic An MQTT topic / Xively Messaging channel.
     * @param qos MQTT qos. The usage of corresponding {@link Config}
     *            constants are encouraged.
     * @return Returns the message id. This id identifies the message in the delivery confirmation callback.
     * The value is -1 on message send error.
     */
    public int publish(String message, String topic, int qos);

    /**
     * Publishes the plain text {@param message} to the specified {@param topic} as an MQTT payload.
     * The message can have QoS specified by @param qos.
     *
     * @param message The mqtt payload.
     * @param topic An MQTT topic / Xively Messaging channel.
     * @param qos MQTT qos. The usage of corresponding {@link Config}
     *            constants are encouraged.
     * @param retained The retain flag of the mqtt message.
     * @return Returns the message id. This id identifies the message in the delivery confirmation callback.
     * The value is -1 on message send error.
     */
    public int publish(byte[] message, String topic, int qos, boolean retained);

    /**
     * Subscribes to a Xively Messaging MQTT topic.
     * QoS will be 1, or as specified in {@link Config}.
     *
     * @param topic A topic Id.
     * @throws MqttException
     */
    public void subscribeToTopic(String topic) throws MqttException;

    /**
     * Subscribes to a Xively Messaging MQTT topic with the specified QoS.
     * QoS values valid in Xively Messaging are specified in {@link Config}.
     *
     * @param qos MQTT QoS value.
     * @throws MqttException
     */
    public void subscribeToTopic(String topic, int qos) throws MqttException;

    /**
     * Unsubscribes from the specified topic.
     *
     * @param topic MQTT topic.
     * @throws MqttException
     */
    public void unsubscribeFromTopic(String topic) throws MqttException;

    /**
     * Adds a {@link ConnectionListener} listener instance for connection related events.
     * If a listener has already been added, no action is taken.
     *
     * @param listener A {@link ConnectionListener} instance.
     */
	public void addConnectionListener(ConnectionListener listener);

    /**
     * Removes the specified listener instance. If it has already been removed or has never
     * been added, no action is taken.
     *
     * @param listener A {@link ConnectionListener} instance.
     */
	public void removeConnectionListener(ConnectionListener listener);

    /**
     * Adds a {@link PublishListener} listener instance for publish delivery callback.
     * If a listener has already been added, no action is taken.
     *
     * @param listener A {@link PublishListener} instance.
     */
	public void addPublishListener(PublishListener listener);

    /**
     * Removes the specified listener instance. If it has already been removed or has never
     * been added, no action is taken.
     *
     * @param listener A {@link PublishListener} instance.
     */
	public void removePublishListener(PublishListener listener);
	
}
