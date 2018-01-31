package com.xively.internal.rest.timeseries;

import com.xively.timeseries.TimeSeriesItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetData {

    @GET("/api/v4/data/{topic}")
    Call<Response> getData(@Path("topic") String topic,
                           @Query("startDateTime") String startDateTime,
                           @Query("endDateTime") String endDateTime,
                           @Query("pageSize") Integer pageSize,
                           @Query("pagingToken") String pagingToken,
                           @Query("omitNull") Boolean omitNull,
                           @Query("category") String category,
                           @Query("groupType") Integer groupType);

    class Response {
        public TimeSeriesListMetaDataDTO meta; //TimeSeries list meta data,
        public TimeSeriesItem[] result; //TimeSeries data
    }

}
