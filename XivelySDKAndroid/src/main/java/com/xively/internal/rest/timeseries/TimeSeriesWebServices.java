package com.xively.internal.rest.timeseries;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.XiCookieManager;
import com.xively.sdk.BuildConfig;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class TimeSeriesWebServices {

    private static final String TAG = "TimeSeriesWebServices";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private String authorizationHeader;
    private final RestAdapter restAdapter;

    public TimeSeriesWebServices() {
        String wsEndpoint;
        if (Config.CONN_USE_SSL){
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.timeseries_endpoint();
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (authorizationHeader != null){
                    request.addHeader("Authorization", authorizationHeader);
                }
            }
        };

        XiCookieManager cookieManager = new XiCookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        cookieManager.setAccessTokenListener(new XiCookieManager.AccessTokenUpdateListener() {
            @Override
            public void onAccessTokenUpdated(String newAccessToken) {
                setBearerAuthorizationHeader(newAccessToken);
                DependencyInjector.get().blueprintWebServices().setBearerAuthorizationHeader(newAccessToken);
            }
        });

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(wsEndpoint)
                .setRequestInterceptor(requestInterceptor)
                .build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else if (LMILog.getMinLogLevel() != XiSdkConfig.LogLevel.OFF){
            restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        } else {
            restAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }
    }

    public void setBearerAuthorizationHeader(String authorization){
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for TimeSeries ws.");
    }

    //for unit testing
    public TimeSeriesWebServices(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }


    public void getData(final String topic, final Date startDateTime, final Date endDateTime,
                        final Callback<GetData.Response> callback) {
        getData(topic, startDateTime, endDateTime, null, null, null, null, null, callback);
    }

    public void getData(final String topic, final Date startDateTime, final Date endDateTime,
                        final Integer pageSize, final String pagingToken, final Boolean omitNull,
                        final String category, final Integer groupType,
                    final Callback<GetData.Response> callback){

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US); //ISO 8601
        final GetData getData = restAdapter.create(GetData.class);

        new Thread(){
            @Override
            public void run() {
                getData.getData(topic,
                        isoFormat.format(startDateTime),
                        isoFormat.format(endDateTime),
                        pageSize, pagingToken, omitNull,
                        category, groupType,
                        callback);
            }
        }.start();
    }
}
