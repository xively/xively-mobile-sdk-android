package com.xively.internal.rest.blueprint;

import android.util.Base64;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.XiCookieManager;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUser;
import com.xively.internal.rest.blueprint.endUserQuery.EndUser;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiDeviceUpdateInfo;
import com.xively.messaging.XiEndUserUpdateInfo;
import com.xively.sdk.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BlueprintWebServices {
    private static final String TAG = "BlueprintWebServices";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    protected enum BluePrintEntity {endUser, accountUser}; //['endUser' or 'device' or 'accountUser']

    private final RestAdapter restAdapter;

    //FIXME: remove header once service accepts proper ID service jwt.
    private String authorizationHeader = null;
    //"Basic ZGV2LXVzZXItZTNkODFmYTE6NzliM2IwNTY1NzE2NDJlODlhOTMwOTc0ODUxMmE1MzM=";//DEV
    //"Basic eGl2ZWx5LXVzZXI6N25tbmsyMzNqazRlITc4";//STAGE

    public BlueprintWebServices() {
        String wsEndpoint;

        if (Config.CONN_USE_SSL){
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.blueprint_endpoint();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (authorizationHeader != null){
                    request.addHeader("authorization", authorizationHeader);
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
                DependencyInjector.get().timeSeriesWebServices().setBearerAuthorizationHeader(newAccessToken);
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

    //for unit testing
    public BlueprintWebServices(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public void setBearerAuthorizationHeader(String authorization){
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for interactions ws.");
    }

    public void setBasicAuthorizationHeader(String authorization){
        this.authorizationHeader = "Basic " + authorization;
        log.d("Auth header set for interactions ws.");
    }

    /**
     *
     * @param accountId    xively account id
     * @param accessUserId *ID user id!*
     * @param callback
     */
    public void getEndUsers(final String accountId, final String accessUserId, final Callback<GetEndUsers.Response> callback){
        final GetEndUsers getEndUser = restAdapter.create(GetEndUsers.class);

        new Thread() {
            @Override
            public void run() {
                getEndUser.getEndUsers(accountId,
                        accessUserId,
                        Boolean.TRUE,
                        Boolean.TRUE,
                        1,
                        10,
                        "asc",
                        callback);
            }
        }.start();
    }

    /**
     *
     * @param accountId xively account id
     * @param accessUserId access user id
     * @param callback
     */
    public void getAccountUser(final String accountId, final String accessUserId,
                               final Callback<GetAccountUser.Response> callback){
        final GetAccountUser getAccountUser = restAdapter.create(GetAccountUser.class);

        new Thread() {
            @Override
            public void run() {
                getAccountUser.getAccountUser(
                        accountId,
                        accessUserId,
                        true,
                        true,
                        1,
                        10,
                        "asc",
                        callback);
            }
        }.start();
    }

    public void createCredentials(final String accountId, final String userId,
        BluePrintEntity entityType,
        final Callback<CreateCredentials.Response> callback){

        final CreateCredentials createCredentials = restAdapter.create(CreateCredentials.class);
        final CreateCredentials.Request request = new CreateCredentials.Request();
        request.accountId = accountId;
        request.entityId = userId;
        request.entityType = entityType.toString();

        new Thread(){
            @Override
            public void run() {
                createCredentials.createCredentials(request, callback);
            }
        }.start();

    }

    public void getEndUserList(final String accountId, final String accountUserId, final int page, final int pageSize, final Callback<GetEndUsers.Response> callback){

        final GetEndUsers getEndUsers = restAdapter.create(GetEndUsers.class);
        new Thread(){
            @Override
            public void run() {
                getEndUsers.getEndUsers(accountId, null, Boolean.TRUE, Boolean.TRUE, page, pageSize, null, callback);
            }}.start();

    }

    public void getEndUser(final String userId, final Callback<GetEndUser.Response> callback){

        final GetEndUser getEndUser = restAdapter.create(GetEndUser.class);

        new Thread(){
            @Override
            public void run() {
                getEndUser.getEndUser(userId,callback);
            }}.start();
    }

    public void putEndUser(final String userId, final String version, final HashMap<String,Object> userData, final Callback<PutEndUser.Response> callback){

        final PutEndUser putEndUser = restAdapter.create(PutEndUser.class);

        new Thread(){
            @Override
            public void run() {
                putEndUser.putEndUser(userId,version,userData,callback);
            }}.start();
    }

    public void getOrganizations(final String accountId, final String parentId, final String deviceTemplateId,
                           final String organizationTemplateId,
                           final int page, final int pageSize, final Callback<GetOrganizations.Response> callback){

        final GetOrganizations getOrganizations = restAdapter.create(GetOrganizations.class);
        new Thread(){
            @Override
            public void run() {
                getOrganizations.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId,
                        null, Boolean.TRUE, Boolean.TRUE, page, pageSize, null, null, callback);
            }}.start();

    }

    public void getOrganization(final String organizationId, final Callback<GetOrganization.Response> callback){

        final GetOrganization getOrganization = restAdapter.create(GetOrganization.class);

        new Thread(){
            @Override
            public void run() {
                getOrganization.getOrganization(organizationId,callback);
            }}.start();
    }

    public void getDevices(final String accountId, final String deviceTemplateId, final String organizationId,
                           final XiDeviceInfo.ProvisioningStateEnum provisioningState,
                           final int page, final int pageSize, final Callback<GetDevices.Response> callback){

        final GetDevices getDevices = restAdapter.create(GetDevices.class);
        new Thread(){
            @Override
            public void run() {
                getDevices.getDevices(accountId, deviceTemplateId, organizationId,
                        provisioningState == null ? null : provisioningState.toString(),
                        Boolean.TRUE, Boolean.TRUE, page, pageSize, null, null, callback);
            }}.start();

    }

    public void getDevice(final String deviceId, final Callback<GetDevice.Response> callback){

        final GetDevice getDevice = restAdapter.create(GetDevice.class);

        new Thread(){
            @Override
            public void run() {
                getDevice.getDevice(deviceId,callback);
            }}.start();
    }

    public void putDevice(final String deviceId, final String version, final HashMap<String,Object> deviceData, final Callback<PutDevice.Response> callback){

        final PutDevice putDevice = restAdapter.create(PutDevice.class);

        new Thread(){
            @Override
            public void run() {
                putDevice.putDevice(deviceId,version,deviceData,callback);
            }}.start();
    }

    //======================= helper methods ======================================================

    public void queryXivelyAccount(String jwt, final Callback<XivelyAccount> callback){
        if (jwt == null){
            callback.failure(null);
            log.e("Failed to acquire credentials: missing token.");
            return;
        }

        String[] jwtSplit = jwt.split("\\.");
        if (jwtSplit.length < 2){
            callback.failure(null);
            log.e("Failed to acquire credentials: corrupt token.");
            return;
        }

        byte[] jwtData = Base64.decode(jwtSplit[1].getBytes(), Base64.DEFAULT);
        if (jwtData == null || jwtData.length < 1){
            callback.failure(null);
            log.e("Failed to acquire credentials: corrupt token.");
            return;
        }

        final String idmUserId;
        final String accountId;

        try {
            JSONObject userData = new JSONObject(new String(jwtData));
            idmUserId = userData.getString("userId");
            accountId = userData.getString("accountId");
        } catch (JSONException ex){
            log.e("Failed to acquire credentials: corrupt token.");
            log.t(ex.toString());
            callback.failure(null);
            return;
        }

        final Callback<GetAccountUser.Response> accountUsersCallback = new Callback<GetAccountUser.Response>() {

            @Override
            public void success(GetAccountUser.Response response, Response response2) {
                if (response == null ||
                        response.accountUsers == null ||
                        response.accountUsers.results.length == 0) {
                    failure(null);
                    return;
                }

                AccountUser resultAccountUser = null;
                for (AccountUser accountUser : response.accountUsers.results){
                    if (accountUser.userId.endsWith(idmUserId)){
                        resultAccountUser = accountUser;
                        break;
                    }
                }

                if (resultAccountUser == null) {
                    failure(null);
                } else {
                    queryCredentials(accountId, resultAccountUser.id, null,
                            BluePrintEntity.accountUser, callback);
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);
            }
        };

        getEndUsers(accountId, idmUserId,
                new Callback<GetEndUsers.Response>() {
                    @Override
                    public void success(GetEndUsers.Response response, Response response2) {
                        if (response == null ||
                                response.endUsers == null ||
                                response.endUsers.size() == 0) {
                            failure(null);
                            return;
                        }

                        EndUser resultEndUser = null;

                        ArrayList<Object> endUserList = (ArrayList<Object>)response.endUsers.get("results");

                        for(Object data: endUserList) {

                            LinkedTreeMap<String, Object> endUserMap = (LinkedTreeMap<String, Object>) data;
                            String userId = (String)endUserMap.get("id");
                            if ( userId.endsWith(idmUserId)) {
                                resultEndUser = new EndUser();
                                resultEndUser.id = (String)endUserMap.get("id");
                                resultEndUser.emailAddress = (String)endUserMap.get("emailAddress");
                                break;
                            }
                        }

                        if (resultEndUser == null) {
                            failure(null);
                        } else {
                            queryCredentials(accountId, resultEndUser.id, null,
                                    BluePrintEntity.endUser, callback);
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        getAccountUser(accountId, idmUserId, accountUsersCallback);
                    }
                });

    }

    private void queryCredentials(final String accountId, String userId, final String name,
                                  BluePrintEntity entityType,
                                  final Callback<XivelyAccount> callback){
        createCredentials(accountId, userId, entityType, new Callback<CreateCredentials.Response>() {
            @Override
            public void success(CreateCredentials.Response response, Response response2) {
                if (response == null ||
                        response.mqttCredential == null){
                    callback.failure(null);
                } else {
                    XivelyAccount result = new XivelyAccount(
                            accountId,
                            response.mqttCredential.entityId,
                            response.mqttCredential.secret,
                            name
                    );
                    response.mqttCredential = null;
                    callback.success(result, response2);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);
            }
        });
    }
}
