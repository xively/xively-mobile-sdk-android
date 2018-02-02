package com.xively.internal.rest.timeseries;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TimeSeriesApiFactory {
    private GetData timeSeriesApi;

    private TimeSeriesApiFactory() {
        String wsEndpoint = "https://" + Config.timeseries_endpoint();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else if (LMILog.getMinLogLevel() != XiSdkConfig.LogLevel.OFF) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        httpClient.addInterceptor(loggingInterceptor);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(wsEndpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        this.timeSeriesApi = retrofit.create(GetData.class);
    }

    public static TimeSeriesApiFactory getInstance() {
        return new TimeSeriesApiFactory();
    }

    public GetData getTimeSeriesApi() {
        return timeSeriesApi;
    }
}
