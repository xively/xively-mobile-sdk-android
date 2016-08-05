package com.xively.internal.rest.access;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import retrofit.Callback;
import retrofit.RestAdapter;

public class AccessWebServices {
    private static final String TAG = "AccessWebServices";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private final RestAdapter restAdapter;

    public AccessWebServices() {
        String wsEndpoint;

        if (Config.CONN_USE_SSL){
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.ACCESS_WS_ENDPOINT;
        restAdapter = new RestAdapter.Builder().setEndpoint(wsEndpoint).build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else if (LMILog.getMinLogLevel() != XiSdkConfig.LogLevel.OFF){
            restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        } else {
            restAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }

    }

    //for unit testing
    public AccessWebServices(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    /**
     * //FIXME: this is deprecated
     */
    public void getOAuthUrl(final String providerId, final Callback<GetOAuthUrl.Response> callback){
        //FIXME: set callback schema once service is defined
        //redirectUri = "xi:" + sdkUserAccountId;

        //FIXME: enable when service is available and remove mock code
        final String redirectUri = "xi123456789://";//"http://imatko-w7.3amlabs.net:8000/loginMock.html";

        GetOAuthUrl.Response response = new GetOAuthUrl.Response();
        response.location = "https://access.dev.xively.us/api/authentication/oauth/authenticate" +
                "?provider_id=" + providerId +
                "&redirect_uri=" + redirectUri;
        callback.success(response, null);
    }
}
