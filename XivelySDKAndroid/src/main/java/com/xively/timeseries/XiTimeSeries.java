package com.xively.timeseries;

import com.xively.XiService;

import java.util.Date;

public interface XiTimeSeries extends XiService {


    /**
     * Request historic data of the specified channel stored in TimeSeries for the given time range.
     *
     * @param channel The Xively Channel Id.
     * @param startDate The start date for the filtering of the time range.
     * @param endDate The end date for the filtering of the time range.
     * @param xiTimeSeriesCallback
     */
    void requestTimeSeriesItemsForChannel(String channel, Date startDate, Date endDate, XiTimeSeriesCallback xiTimeSeriesCallback);

    /**
     * Request historic data of the specified channel stored in TimeSeries for the given time range.
     *
     * @param channel The Xively Channel Id.
     * @param startDate The start date for the filtering of the time range.
     * @param endDate The end date for the filtering of the time range.
     * @param category Retrieve only a single category of archived data.
     * @param xiTimeSeriesCallback
     */
    void requestTimeSeriesItemsForChannel(String channel, Date startDate, Date endDate, String category, XiTimeSeriesCallback xiTimeSeriesCallback);

    /**
     * Cancel the current pending request.
     * Does nothing if there is no pending request or if it has already been canceled or finished.
     */
    void cancel();

}
