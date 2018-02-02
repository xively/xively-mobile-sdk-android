package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BlueprintApiFactory {
    private CreateCredentials createMqttCredentialsApi;

    private GetAccountUser getAccountUserApi;

    private GetDevice getDeviceApi;
    private PutDevice putDeviceApi;
    private GetDevices getDevicesApi;

    private GetEndUser getEndUserApi;
    private PutEndUser putEndUserApi;
    private GetEndUsers getEndUsersApi;

    private GetOrganization getOrganizationApi;
    private GetOrganizations getOrganizationsApi;

    private BlueprintApiFactory() {
        String wsEndpoint;

        if (Config.CONN_USE_SSL) {
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.blueprint_endpoint();


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

        this.createMqttCredentialsApi = retrofit.create(CreateCredentials.class);
        this.getAccountUserApi = retrofit.create(GetAccountUser.class);

        this.getDeviceApi = retrofit.create(GetDevice.class);
        this.putDeviceApi = retrofit.create(PutDevice.class);
        this.getDevicesApi = retrofit.create(GetDevices.class);

        this.getEndUserApi = retrofit.create(GetEndUser.class);
        this.putEndUserApi = retrofit.create(PutEndUser.class);
        this.getEndUsersApi = retrofit.create(GetEndUsers.class);

        this.getOrganizationApi = retrofit.create(GetOrganization.class);
        this.getOrganizationsApi = retrofit.create(GetOrganizations.class);
    }

    public static BlueprintApiFactory getInstance() {
        return new BlueprintApiFactory();
    }

    public CreateCredentials getCreateMqttCredentialsApi() {
        return createMqttCredentialsApi;
    }

    public GetAccountUser getGetAccountUserApi() {
        return getAccountUserApi;
    }

    public GetDevice getGetDeviceApi() {
        return getDeviceApi;
    }

    public PutDevice getPutDeviceApi() {
        return putDeviceApi;
    }

    public GetDevices getGetDevicesApi() {
        return getDevicesApi;
    }

    public GetEndUser getGetEndUserApi() {
        return getEndUserApi;
    }

    public PutEndUser getPutEndUserApi() {
        return putEndUserApi;
    }

    public GetEndUsers getGetEndUsersApi() {
        return getEndUsersApi;
    }

    public GetOrganization getGetOrganizationApi() {
        return getOrganizationApi;
    }

    public GetOrganizations getGetOrganizationsApi() {
        return getOrganizationsApi;
    }
}
