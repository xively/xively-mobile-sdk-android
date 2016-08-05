package com.xively;

/**
 * Generic service creator callback.
 *
 * @param <T> The initialized service (TimeSeries, Messaging etc.).
 */
public interface XiServiceCreatorCallback<T extends XiService>{

    /**
     * Listener for asynchronous service creation.
     * The resulting service object {@link com.xively.messaging.XiMessaging}, {@link com.xively.timeseries.XiTimeSeries} etc.)
     * is being passed in the @param service.
     *
     * @param service The service object of the requested Xively Service type.
     *                The callback happens when this service is fully initialized and ready for usage.
     */
    void onServiceCreated(T service);

    /**
     * The service creation has failed.
     */
    void onServiceCreateFailed();
}
