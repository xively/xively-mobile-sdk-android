package com.xively.internal.rest.timeseries;

import com.xively.timeseries.TimeSeriesItem;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface GetData {

    @GET("/api/v4/data/{topic}")
    void getData(@Path("topic") String topic,
                 @Query("startDateTime") String startDateTime,
                 @Query("endDateTime") String endDateTime,
                 @Query("pageSize") Integer pageSize,
                 @Query("pagingToken") String pagingToken,
                 @Query("omitNull") Boolean omitNull,
                 @Query("category") String category,
                 @Query("groupType") Integer groupType,
                 Callback<Response> callback);

    class Response {
        public TimeSeriesListMetaDataDTO meta; //TimeSeries list meta data,
        public TimeSeriesItem[] result; //TimeSeries data
    }

}
