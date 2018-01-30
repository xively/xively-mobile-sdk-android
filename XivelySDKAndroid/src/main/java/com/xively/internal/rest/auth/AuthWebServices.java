package com.xively.internal.rest.auth;

import com.xively.internal.logger.LMILog;

import retrofit2.Callback;

public class AuthWebServices {
    private static final String TAG = "AuthWebServices";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private final LoginUser loginApi;

    public AuthWebServices() {
        AuthApiFactory authApi = AuthApiFactory.getInstance();
        this.loginApi = authApi.getLoginApi();
    }

    //for unit testing
    public AuthWebServices(LoginUser loginApi) {
        this.loginApi = loginApi;
    }

    public void loginUser(String emailAddress, String password, String accountId, final Callback<LoginUser.Response> callback) {
        final LoginUser.Request request = new LoginUser.Request();
        request.emailAddress = emailAddress;
        request.password = password;
        request.accountId = accountId;

        this.loginApi.loginUser(request).enqueue(callback);
    }
}
