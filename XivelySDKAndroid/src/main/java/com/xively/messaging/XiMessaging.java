package com.xively.messaging;

import com.xively.XiException;
import com.xively.XiService;

/**
 * Provides basic IoT messaging features to send and receive messages.
 */
public interface XiMessaging extends XiService {

    /**
     * Internal state of the Messaging service.
     * Running      - The service is connected and ready for all operations.
     * Reconnecting - The underlying connection was lost and it is reconnecting.
     *                No publish or subscribe operation can be made in this state.
     * Closed       - The service is closed an no operations are available any more.
     * Error        - The service is terminated in an error state and no operations
     *                are available any more.
     */
    enum State {Running, Reconnecting, Closed, Error}

    /**
     * QoS values channel subscribe and data publish.
     * AtMostOnce  - Data is delivered at most once. Delivery is not guaranteed to all subscribers,
     *               some data occasionally might not be delivered.
     * AtLeastOnce - Data is delivered at least once. Delivery is guaranteed for all subscribers,
     *               however some data occasionally might be delivered more then once.
     * ExactlyOnce - Data is delivered exactly once. The delivery of all data is guaranteed for
     *               all subscribers and all data is delivered exactly once.
     */
    enum XiMessagingQoS {AtMostOnce, AtLeastOnce, ExactlyOnce}

    /**
     * Returns the current state of the Messaging service.
     *
     * @return A {@link XiMessaging.State} value.
     */
    State getState();

    /**
     * Add a listener for data send and receive events. If a listener instance
     * is already added, further add invocations do nothing.
     *
     * @param listener The {@link XiMessagingDataListener} instance to be added.
     */
    void addDataListener(XiMessagingDataListener listener);

    /**
     * Removes a registered data listener instance.
     * No further event callbacks will be made to this data listener.
     *
     * This method does nothing if a listener has been already removed or has never been added
     * to the current messaging service instance.
     *
     * @param listener The {@link XiMessagingDataListener} instance to be removed.
     */
    void removeDataListener(XiMessagingDataListener listener);

    /**
     * Add a listener to monitor state changes of the messaging service.
     * If a listener has already been added, further invocations of the add method will do nothing.
     *
     * @param listener An {@link XiMessagingStateListener} instance.
     */
    void addStateListener(XiMessagingStateListener listener);

    /**
     * Removes a registered state listener instance.
     * No further status event callbacks will be made to this data listener.
     *
     * This method does nothing if a listener has been already removed or has never been added
     * to the current messaging service instance.
     *
     * @param listener An {@link XiMessagingStateListener} instance.
     */
    void removeStateListener(XiMessagingStateListener listener);

    /**
     * Register a listener for message channel subscription events.
     * The listener provides callbacks for subscription failure and success.
     *
     * @param listener An {@link XiMessagingSubscriptionListener} instance.
     */
    void addSubscriptionListener(XiMessagingSubscriptionListener listener);

    /**
     * Removes a subscription listener from the current messaging service instance.
     * The listener will receive no further subscription event callbacks.
     *
     * This method does nothing if a listener has been already removed or has never been added
     * to the current messaging service instance.
     *
     * @param listener An {@link XiMessagingSubscriptionListener} instance.
     */
    void removeSubscriptionListener(XiMessagingSubscriptionListener listener);

    /**
     * Publish a message to the specified channel of an IoT device.
     *
     * @param channel The messaging channel for the message.
     * @param message The message as a byte array.
     * @param qos The QoS value of the publish. For details see {@link XiMessagingQoS}.
     *
     * @return The id of the message is returned or -1 if delivery confirmation is not supported
     * for the current connection or messaging channel.
     * The delivery confirmation event can be monitored using the {@link XiMessagingDataListener}'s
     * onDataSent callback.
     *
     * @throws XiException.NotConnectedException Publish will throw a {@link com.xively.XiException.NotConnectedException}
     * exception if the underlying connection is not connected, respectively the Messaging service
     * is not in Running state.
     */
    int publish(String channel, byte[] message, XiMessagingQoS qos) throws XiException.NotConnectedException;

    /**
     * Publish a message to the specified channel of an IoT device.
     *
     * @param channel The messaging channel for the message.
     * @param message The message as a byte array.
     * @param qos The QoS value of the publish. For details see {@link XiMessagingQoS}.
     * @param retain The retain flag of the message.
     *
     * @return The id of the message is returned or -1 if delivery confirmation is not supported
     * for the current connection or messaging channel.
     * The delivery confirmation event can be monitored using the {@link XiMessagingDataListener}'s
     * onDataSent callback.
     *
     * @throws XiException.NotConnectedException Publish will throw a {@link com.xively.XiException.NotConnectedException}
     * exception if the underlying connection is not connected, respectively the Messaging service
     * is not in Running state.
     */
    int publish(String channel, byte[] message, XiMessagingQoS qos, boolean retain) throws XiException.NotConnectedException;

    /**
     * Subscribe to receive publishes on a specified IoT device messaging channel.
     * XiMessagingSubscriptionListener listeners can be used to monitor the success or failure
     * of the subscribe operation.
     *
     * @param channel The channel of your IoT device.
     * @param qos The QoS value for the current subscription.
     *            For details see {@link XiMessagingQoS}.
     *
     * @throws XiException.NotConnectedException This method will throw
     * a {@link com.xively.XiException.NotConnectedException} if the underlying connection
     * is not connected, respectively the Messaging service is not in Running state.
     *
     * @throws XiException.ConnectionException This method will throw
     * a {@link com.xively.XiException.ConnectionException} if the subscribe operation has immediately failed.
     */
    void subscribe(String channel, XiMessagingQoS qos) throws XiException.NotConnectedException, XiException.ConnectionException;

    /**
     * Unsubscribes from the specified IoT device messaging channel.
     * No further data events will be monitored on this channel.
     *
     * XiMessagingSubscriptionListener listeners can be used to monitor the success or failure
     * of the unsubscribe operation.
     *
     * @param channel The device messaging channel.
     *
     * @throws XiException.NotConnectedException This method will throw
     * a {@link com.xively.XiException.NotConnectedException} if the underlying connection
     * is not connected, respectively the Messaging service is not in Running state.
     *
     * @throws XiException.ConnectionException This method will throw
     * a {@link com.xively.XiException.ConnectionException} if the unsubscribe operation has immediately failed.
     */
    void unsubscribe(String channel) throws XiException.NotConnectedException, XiException.ConnectionException;

    /**
     * Closes the current messaging service instance.
     * For a new Messaging connection a new Messaging Creator must be obtained
     * from the current {@link com.xively.XiSession} instance.
     */
    void close();

}
