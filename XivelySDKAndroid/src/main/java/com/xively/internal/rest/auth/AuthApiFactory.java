package com.xively.internal.rest.auth;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AuthApiFactory {
    private LoginUser loginApi;

    private AuthApiFactory() {
        String wsEndpoint = "https://" + Config.auth_endpoint();

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

        this.loginApi = retrofit.create(LoginUser.class);
    }

    public static AuthApiFactory getInstance() {
        return new AuthApiFactory();
    }

    public LoginUser getLoginApi() {
        return this.loginApi;
    }
}
