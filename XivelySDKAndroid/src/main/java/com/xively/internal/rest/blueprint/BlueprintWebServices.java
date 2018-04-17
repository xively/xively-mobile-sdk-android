package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUser;
import com.xively.internal.rest.blueprint.endUserQuery.EndUser;
import com.xively.messaging.XiDeviceInfo;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BlueprintWebServices {
    private static final String TAG = "BlueprintWebServices";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    protected enum BluePrintEntity {endUser, accountUser}

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

    private String authorizationHeader = null;

    public BlueprintWebServices() {
        BlueprintApiFactory blueprintApiFactory = BlueprintApiFactory.getInstance();
        this.createMqttCredentialsApi = blueprintApiFactory.getCreateMqttCredentialsApi();
        this.getAccountUserApi = blueprintApiFactory.getGetAccountUserApi();
        this.getDeviceApi = blueprintApiFactory.getGetDeviceApi();
        this.putDeviceApi = blueprintApiFactory.getPutDeviceApi();
        this.getDevicesApi = blueprintApiFactory.getGetDevicesApi();
        this.getEndUserApi = blueprintApiFactory.getGetEndUserApi();
        this.putEndUserApi = blueprintApiFactory.getPutEndUserApi();
        this.getEndUsersApi = blueprintApiFactory.getGetEndUsersApi();
        this.getOrganizationApi = blueprintApiFactory.getGetOrganizationApi();
        this.getOrganizationsApi = blueprintApiFactory.getGetOrganizationsApi();
    }

    //for unit testing
    public BlueprintWebServices(
            CreateCredentials createMqttCredentialsApi,
            GetAccountUser getAccountUserApi,
            GetDevice getDeviceApi,
            PutDevice putDeviceApi,
            GetDevices getDevicesApi,
            GetEndUser getEndUserApi,
            PutEndUser putEndUserApi,
            GetEndUsers getEndUsersApi,
            GetOrganization getOrganizationApi,
            GetOrganizations getOrganizationsApi
    ) {
        this.createMqttCredentialsApi = createMqttCredentialsApi;
        this.getAccountUserApi = getAccountUserApi;
        this.getDeviceApi = getDeviceApi;
        this.putDeviceApi = putDeviceApi;
        this.getDevicesApi = getDevicesApi;
        this.getEndUserApi = getEndUserApi;
        this.putEndUserApi = putEndUserApi;
        this.getEndUsersApi = getEndUsersApi;
        this.getOrganizationApi = getOrganizationApi;
        this.getOrganizationsApi = getOrganizationsApi;
    }

    public void setBearerAuthorizationHeader(String authorization) {
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for interactions ws.");
    }

    /**
     * @param accountId    xively account id
     * @param accessUserId *ID user id!*
     * @param callback
     */
    public void getEndUsers(
            final String accountId,
            final String accessUserId,
            final Callback<GetEndUsers.Response> callback
    ) {
        this.getEndUsersApi.getEndUsers(
                authorizationHeader,
                accountId,
                accessUserId,
                Boolean.TRUE,
                Boolean.TRUE,
                1,
                10,
                "asc"
        ).enqueue(callback);
    }

    /**
     * @param accountId    xively account id
     * @param accessUserId access user id
     * @param callback
     */
    public void getAccountUser(
            final String accountId,
            final String accessUserId,
            final Callback<GetAccountUser.Response> callback
    ) {
        this.getAccountUserApi.getAccountUser(
                authorizationHeader,
                accountId,
                accessUserId,
                Boolean.TRUE,
                Boolean.TRUE,
                1,
                10,
                "asc"
        ).enqueue(callback);
    }

    public void createCredentials(
            final String accountId,
            final String userId,
            BluePrintEntity entityType,
            final Callback<CreateCredentials.Response> callback
    ) {
        final CreateCredentials.Request request = new CreateCredentials.Request();
        request.accountId = accountId;
        request.entityId = userId;
        request.entityType = entityType.toString();

        this.createMqttCredentialsApi.createCredentials(
                authorizationHeader,
                request
        ).enqueue(callback);
    }

    public void getEndUserList(
            final String accountId,
            final String accountUserId,
            final int page,
            final int pageSize,
            final Callback<GetEndUsers.Response> callback
    ) {
        this.getEndUsersApi.getEndUsers(
                authorizationHeader,
                accountId,
                null,
                Boolean.TRUE,
                Boolean.TRUE,
                page,
                pageSize,
                null
        ).enqueue(callback);
    }

    public void getEndUser(final String userId, final Callback<GetEndUser.Response> callback) {
        this.getEndUserApi.getEndUser(
                authorizationHeader,
                userId
        ).enqueue(callback);
    }

    public void putEndUser(
            final String userId,
            final String version,
            final HashMap<String, Object> userData,
            final Callback<PutEndUser.Response> callback
    ) {
        this.putEndUserApi.putEndUser(
                authorizationHeader,
                userId,
                version,
                userData
        ).enqueue(callback);
    }

    public void getOrganizations(
            final String accountId,
            final String parentId,
            final String deviceTemplateId,
            final String organizationTemplateId,
            final int page,
            final int pageSize,
            final Callback<GetOrganizations.Response> callback
    ) {
        this.getOrganizationsApi.getOrganizations(
                authorizationHeader,
                accountId,
                parentId,
                deviceTemplateId,
                organizationTemplateId,
                null,
                Boolean.TRUE,
                Boolean.TRUE,
                page,
                pageSize,
                null,
                null
        ).enqueue(callback);
    }

    public void getOrganization(
            final String organizationId,
            final Callback<GetOrganization.Response> callback
    ) {
        this.getOrganizationApi.getOrganization(
                authorizationHeader,
                organizationId
        ).enqueue(callback);
    }

    public void getDevices(
            final String accountId,
            final String deviceTemplateId,
            final String organizationId,
            final XiDeviceInfo.ProvisioningStateEnum provisioningState,
            final int page,
            final int pageSize,
            final Callback<GetDevices.Response> callback
    ) {
        this.getDevicesApi.getDevices(
                authorizationHeader,
                accountId,
                deviceTemplateId,
                organizationId,
                provisioningState == null ? null : provisioningState.toString(),
                Boolean.TRUE,
                Boolean.TRUE,
                page,
                pageSize,
                null,
                null
        ).enqueue(callback);
    }

    public void getDevice(final String deviceId, final Callback<GetDevice.Response> callback) {
        this.getDeviceApi.getDevice(
                authorizationHeader,
                deviceId
        ).enqueue(callback);
    }

    public void putDevice(
            final String deviceId,
            final String version,
            final HashMap<String, Object> deviceData,
            final Callback<PutDevice.Response> callback
    ) {
        this.putDeviceApi.putDevice(
                authorizationHeader,
                deviceId,
                version,
                deviceData
        ).enqueue(callback);
    }

    //======================= helper methods ======================================================

    public void queryXivelyAccount(String jwt, final Callback<XivelyAccount> callback) {
        if (jwt == null) {
            log.e("Failed to acquire credentials: missing token.");
            callback.onFailure(null, new Throwable("Missing JWT"));
            return;
        }

        String[] jwtSplit = jwt.split("\\.");
        if (jwtSplit.length < 2) {
            log.e("Failed to acquire credentials: corrupt token.");
            callback.onFailure(null, new Throwable("Corrupt Token"));
            return;
        }

        final String idmUserId;
        final String accountId;

        try {
            // We only need the payload of the JWT which contains the context
            SigningKeyResolver notVerifySignatureResolver = new SigningKeyResolver() {
                @Override
                public Key resolveSigningKey(JwsHeader header, Claims claims) {
                    return null;
                }

                @Override
                public Key resolveSigningKey(JwsHeader header, String plaintext) {
                    return null;
                }
            };

            Claims claims = Jwts.parser()
                    .setSigningKeyResolver(notVerifySignatureResolver)
                    .parseClaimsJwt(jwtSplit[0] + "." + jwtSplit[1] + ".").getBody();

            idmUserId = claims.get("userId", String.class);
            accountId = claims.get("accountId", String.class);
        } catch (UnsupportedJwtException ex) {
            log.e("Failed to acquire credentials: Could not parse JWT Payload");
            callback.onFailure(null, new Throwable("Could not parse JWT Payload"));
            return;
        }

        if (accountId == null || idmUserId == null) {
            log.e("Failed to parse accountId or userId from JWT");
            callback.onFailure(null, new Throwable("Failed to parse accountId or userId from JWT"));
        }

        final Callback<GetAccountUser.Response> accountUsersCallback = new Callback<GetAccountUser.Response>() {

            @Override
            public void onResponse(
                    Call<GetAccountUser.Response> call,
                    retrofit2.Response<GetAccountUser.Response> response
            ) {
                GetAccountUser.Response accountUsersResponse = response.body();
                if (accountUsersResponse != null) {
                    log.d(accountUsersResponse.toString());   
                }

                if (accountUsersResponse == null ||
                        accountUsersResponse.accountUsers == null ||
                        accountUsersResponse.accountUsers.results.length == 0) {
                    onFailure(call, new Throwable("Empty AccountUser response"));
                    return;
                }

                AccountUser resultAccountUser = null;
                for (AccountUser accountUser : accountUsersResponse.accountUsers.results) {
                    if (accountUser.userId.endsWith(idmUserId)) {
                        resultAccountUser = accountUser;
                        break;
                    }
                }

                if (resultAccountUser == null) {
                    onFailure(call, new Throwable("Empty AccountUser response"));
                } else {
                    queryCredentials(accountId, resultAccountUser.id, null,
                            BluePrintEntity.accountUser, callback);
                }
            }

            @Override
            public void onFailure(Call<GetAccountUser.Response> call, Throwable t) {
                callback.onFailure(null, t);
            }
        };

        getEndUsers(accountId, idmUserId, new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(
                    Call<GetEndUsers.Response> call,
                    retrofit2.Response<GetEndUsers.Response> response
            ) {
                GetEndUsers.Response endUsersResponse = response.body();
                log.d(endUsersResponse.toString());

                if (endUsersResponse == null ||
                        endUsersResponse.endUsers == null ||
                        endUsersResponse.endUsers.size() == 0) {
                    onFailure(call, new Throwable("Empty EndUsers response"));
                    return;
                }

                EndUser resultEndUser = null;

                ArrayList<Object> endUserList = (ArrayList<Object>) endUsersResponse.endUsers.get("results");

                for (Object data : endUserList) {
                    LinkedTreeMap<String, Object> endUserMap = (LinkedTreeMap<String, Object>) data;
                    String userId = (String) endUserMap.get("userId");

                    if (userId.endsWith(idmUserId)) {
                        resultEndUser = new EndUser();
                        resultEndUser.id = (String) endUserMap.get("id");
                        resultEndUser.emailAddress = (String) endUserMap.get("emailAddress");
                        log.i(resultEndUser.toString());
                        break;
                    }
                }

                if (resultEndUser == null) {
                    onFailure(call, new Throwable("Empty EndUsers response"));
                } else {
                    queryCredentials(accountId, resultEndUser.id, null,
                            BluePrintEntity.endUser, callback);
                }
            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {
                getAccountUser(accountId, idmUserId, accountUsersCallback);
            }
        });

    }

    private void queryCredentials(
            final String accountId,
            String userId,
            final String name,
            BluePrintEntity entityType,
            final Callback<XivelyAccount> callback
    ) {
        createCredentials(accountId, userId, entityType, new Callback<CreateCredentials.Response>() {
            @Override
            public void onResponse(
                    Call<CreateCredentials.Response> call,
                    retrofit2.Response<CreateCredentials.Response> response
            ) {
                CreateCredentials.Response credentialResponse = response.body();
                log.d(credentialResponse.toString());

                if (credentialResponse == null ||
                        credentialResponse.mqttCredential == null) {
                    onFailure(call, new Throwable("Empty Credentials response"));
                } else {
                    XivelyAccount result = new XivelyAccount(
                            accountId,
                            credentialResponse.mqttCredential.entityId,
                            credentialResponse.mqttCredential.secret,
                            name
                    );
                    credentialResponse.mqttCredential = null;
                    callback.onResponse(null, Response.success(result));
                }
            }

            @Override
            public void onFailure(Call<CreateCredentials.Response> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }
}
