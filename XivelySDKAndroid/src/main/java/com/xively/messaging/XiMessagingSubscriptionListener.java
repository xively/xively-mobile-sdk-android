package com.xively.messaging;

/**
 * Channel subscription listener interface for {@link XiMessaging}.
 */
public interface XiMessagingSubscriptionListener {

    /**
     * Event for successful subscribe to a messaging channel.
     *
     * @param channelId the Id of the channel.
     */
    void onSubscribed(String channelId);

    /**
     * Event for channel subscribe failure.
     *
     * @param channelId the Id of the channel.
     */
    void onSubscribeFailed(String channelId);

    /**
     * Event for successful unsubscribe from a messaging channel.
     *
     * @param channelId the Id of the channel.
     */
    void onUnsubscribed(String channelId);

    /**
     * Event for channel unsubscribe failure.
     *
     * @param channelId the Id of the channel.
     */
    void onUnsubscribeFailed(String channelId);
}
