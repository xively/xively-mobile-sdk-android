package com.xively.messaging;

/**
 * Data listener interface for {@link XiMessaging}.
 */
public interface XiMessagingDataListener {

    /**
     * Event for data receive on the specified IoT messaging channel.
     *
     * @param data the payload of the message.
     * @param channel the messaging channel's id.
     */
    void onDataReceived(byte[] data, String channel);

    /**
     * Delivery confirmation event.
     * The message with the specified id has been successfully published.
     *
     * @param messageId
     */
    void onDataSent(int messageId);
}
