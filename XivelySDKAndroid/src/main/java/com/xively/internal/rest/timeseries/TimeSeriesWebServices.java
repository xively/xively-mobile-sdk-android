package com.xively.internal.rest.timeseries;

import com.xively.internal.logger.LMILog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Callback;


public class TimeSeriesWebServices {

    private static final String TAG = "TimeSeriesWebServices";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private final GetData timeseriesApi;
    private String authorizationHeader;

    public TimeSeriesWebServices() {
        TimesSeriesApiFactory timesSeriesApiFactory = TimesSeriesApiFactory.getInstance();
        this.timeseriesApi = timesSeriesApiFactory.getTimeseriesApi();
    }

    //for unit testing
    public TimeSeriesWebServices(GetData timeseriesApi) {
        this.timeseriesApi = timeseriesApi;
    }

    public void setBearerAuthorizationHeader(String authorization) {
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for TimeSeries ws.");
    }


    public void getData(final String topic, final Date startDateTime, final Date endDateTime,
                        final Callback<GetData.Response> callback) {
        getData(topic, startDateTime, endDateTime, null, null, null, null, null, callback);
    }

    public void getData(final String topic, final Date startDateTime, final Date endDateTime,
                        final Integer pageSize, final String pagingToken, final Boolean omitNull,
                        final String category, final Integer groupType,
                        final Callback<GetData.Response> callback) {

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US); //ISO 8601

        this.timeseriesApi.getData(
                authorizationHeader,
                topic,
                isoFormat.format(startDateTime),
                isoFormat.format(endDateTime),
                pageSize, pagingToken, omitNull,
                category, groupType
        ).enqueue(callback);
    }
}
