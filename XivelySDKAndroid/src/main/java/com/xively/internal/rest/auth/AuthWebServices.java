package com.xively.internal.rest.auth;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import retrofit.Callback;
import retrofit.RestAdapter;

public class AuthWebServices {
    private static final String TAG = "AuthWebServices";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private final RestAdapter restAdapter;

    public AuthWebServices() {
        String wsEndpoint;

        if (Config.CONN_USE_SSL){
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.auth_endpoint();
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
    public AuthWebServices(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public void loginUser(String emailAddress, String password, String accountId, final Callback<LoginUser.Response> callback){
        final LoginUser.Request request = new LoginUser.Request();
        request.emailAddress = emailAddress;
        request.password = password;
        request.accountId = accountId;

        final LoginUser loginUser = restAdapter.create(LoginUser.class);
        loginUser.loginUser(request, callback);
    }
}
