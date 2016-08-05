package com.xively.internal.connection;

/**
 * Listener for Xively Messaging publishes.
 */
public interface PublishListener {

    /**
     * A Xively Messaging publish.
     *
     * @param publish The message payload.
     * @param topic The topic of the publish.
     */
	public void onPublishReceived(String publish, String topic);

    /**
     * Callback for message delivery confirmation.
     *
     * @param messageId the id of the message which the confirmation belongs.
     */
    public void onMessageDeliveryConfirmation(int messageId);
}
