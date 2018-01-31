package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.XiSdkConfig;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUser;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUsersList;
import com.xively.internal.rest.blueprint.credentialsCreate.Credential;

import junit.framework.TestCase;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintMqttCredentialProvisionTest extends TestCase {

    @Mock
    private Callback<XivelyAccount> mockCallback;
    @Mock
    private GetEndUsers mockGetEndUsers;
    @Mock
    private GetAccountUser mockGetAccountUser;
    @Mock
    private CreateCredentials mockCreateCredentials;
    @Captor
    private ArgumentCaptor<Response<XivelyAccount>> captorXivelyAccountResponse;

    private final String corruptJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.somethingUnexpected.MHJi37FA0L1omJenUb-9oie7MyD_dK7K_OzZLfX_Wjr7NC07Yc8EtlnUPDa3uvn5rnDn-CLfn5-8gpHSnZC8wVUbB8axVdReaIOrbVxrMF5DWWrfT2sur71XO0O2Fiihb51DKiTv99nQeBQ0i9N_lfz9XPHdKeavlt32dS14dK3dkFQ_JDd3t7ua3QyVdT4MehOshW7KEvKYzkNLEIRYjgkTocafFnLgA0Bvc-YBWvhZDNuh5GsHCBNu0KTe3dUo2WVrVwTAOtEoUG0CpUzhKvbdYs4Okz8PqLaa9hnZKgIlXHpTxbW4Y8JvvWtqmIZakGPovEYqw9Qm1Dpz0rLM9g";
    private final String testJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9." +
            "eyJpZCI6Im1vY2sgaWQiLCJ1c2VySWQiOiJtb2NrIHVzZXIgaWQiLCJleHBpcmVzIjoxNDM4OTM1\n" +
            "NTA1Njk4LCJyZW5ld2FsS2V5IjoiT28vc29tZXRoaW5nPT0iLCJhY2NvdW50SWQiOiJtb2NrIGFj\n" +
            "Y291bnQgaWQiLCJjZXJ0IjoiY2VydCBkYXRhIn0=" +
            ".MHJi37FA0L1omJenUb-9oie7MyD_dK7K_OzZLfX_Wjr7NC07Yc8EtlnUPDa3uvn5rnDn-CLfn5-8gpHSnZC8wVUbB8axVdReaIOrbVxrMF5DWWrfT2sur71XO0O2Fiihb51DKiTv99nQeBQ0i9N_lfz9XPHdKeavlt32dS14dK3dkFQ_JDd3t7ua3QyVdT4MehOshW7KEvKYzkNLEIRYjgkTocafFnLgA0Bvc-YBWvhZDNuh5GsHCBNu0KTe3dUo2WVrVwTAOtEoUG0CpUzhKvbdYs4Okz8PqLaa9hnZKgIlXHpTxbW4Y8JvvWtqmIZakGPovEYqw9Qm1Dpz0rLM9g";

    /**
     * {"typ":"JWT","alg":"RS256"}{"id":"mock account Id","userId":"mock user Id",
     * "expires":1438935505698,"renewalKey":"Oo/Zj30ETdj1DVR8B7Pu7g==",
     * "accountId":"bbeaee8d-2590-4af0-9673-ac2fd6193007","cert":"cert data"}
     */

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnNullJwt() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        SUT.queryXivelyAccount(null, mockCallback);

        verify(mockCallback, times(1)).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, never()).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                Matchers.<Response<XivelyAccount>>anyObject()
        );
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnInvalidJwt() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        SUT.queryXivelyAccount("something wrong", mockCallback);

        verify(mockCallback, times(1)).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, never()).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                Matchers.<Response<XivelyAccount>>anyObject()
        );
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnCorruptJwtData() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        SUT.queryXivelyAccount(corruptJwt, mockCallback);

        verify(mockCallback, times(1)).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, never()).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                Matchers.<Response<XivelyAccount>>anyObject()
        );
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnGetEndUserFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new FailureEndUsersStubCall());

        when(mockGetAccountUser.getAccountUser(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new FailureAccountUserStubCall());

        SUT.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUsers).getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockGetAccountUser).getAccountUser(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockCallback, times(1)).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );
        verify(mockCallback, never()).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                Matchers.<Response<XivelyAccount>>anyObject()
        );
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnCreateCredentialsFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        GetEndUsers.Response mockGetEndUserResponse = new GetEndUsers.Response();
        mockGetEndUserResponse.endUsers = new HashMap<>();
        mockGetEndUserResponse.endUsers = new HashMap<>();

        LinkedTreeMap<String, Object> endUserMap = new LinkedTreeMap<>();
        endUserMap.put("id", "mock user id");
        endUserMap.put("userId", "mock user id");

        ArrayList<LinkedTreeMap<String, Object>> endUserList = new ArrayList<>();
        endUserList.add(endUserMap);

        mockGetEndUserResponse.endUsers.put("results", endUserList);

        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new SuccessEndUsersStubCall(mockGetEndUserResponse));

        when(mockCreateCredentials.createCredentials(
                Matchers.<CreateCredentials.Request>any()
        )).thenReturn(new FailureCredentialStubCall());

        SUT.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUsers).getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockCreateCredentials).createCredentials(
                Matchers.<CreateCredentials.Request>anyObject()
        );

        verify(mockCallback, times(1)).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, never()).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                Matchers.<Response<XivelyAccount>>anyObject()
        );
    }

    @Test
    public void testQueryXiEndUserAccountSuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        GetAccountUser.Response mockGetAccountUserResponse = new GetAccountUser.Response();
        mockGetAccountUserResponse.accountUsers = new AccountUsersList();
        mockGetAccountUserResponse.accountUsers.results = new AccountUser[1];
        mockGetAccountUserResponse.accountUsers.results[0] = new AccountUser();
        mockGetAccountUserResponse.accountUsers.results[0].id = "Béla";
        mockGetAccountUserResponse.accountUsers.results[0].userId
                = "blueprint tudja minek ide prefix/mock user id";//this id must match the one from the jwt

        CreateCredentials.Response mockCredentialsResponse = new CreateCredentials.Response();
        mockCredentialsResponse.mqttCredential = new Credential();
        mockCredentialsResponse.mqttCredential.entityId = "end user id";
        mockCredentialsResponse.mqttCredential.secret = "end user secret";

        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new FailureEndUsersStubCall());

        when(mockGetAccountUser.getAccountUser(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new SuccessAccountUserStubCall(mockGetAccountUserResponse));

        when(mockCreateCredentials.createCredentials(
                Matchers.<CreateCredentials.Request>any()
        )).thenReturn(new SuccessCredentialStubCall(mockCredentialsResponse));

        SUT.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUsers).getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockGetAccountUser).getAccountUser(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockCreateCredentials).createCredentials(
                Matchers.<CreateCredentials.Request>anyObject()
        );

        verify(mockCallback, never()).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, times(1)).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                captorXivelyAccountResponse.capture()
        );

        XivelyAccount result = captorXivelyAccountResponse.getValue().body();
        assertNotNull(result);
        assertEquals("mock account id", result.getClientId());
        assertNull(result.getDisplayName());
        assertEquals("end user id", result.getUserName());
        assertEquals("end user secret", result.getPassword());
    }

    @Test
    public void testQueryXiAccountUserSuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );

        GetEndUsers.Response mockGetEndUserResponse = new GetEndUsers.Response();
        mockGetEndUserResponse.endUsers = new HashMap<>();

        LinkedTreeMap<String, Object> endUserMap = new LinkedTreeMap<>();
        endUserMap.put("id", "mock user id");
        endUserMap.put("userId", "blueprint tudja minek ide prefix/mock user id");

        ArrayList<Object> endUserList = new ArrayList<>();
        endUserList.add(endUserMap);

        mockGetEndUserResponse.endUsers.put("results", endUserList);

        CreateCredentials.Response mockCredentialsResponse = new CreateCredentials.Response();
        mockCredentialsResponse.mqttCredential = new Credential();
        mockCredentialsResponse.mqttCredential.entityId = "end user id";
        mockCredentialsResponse.mqttCredential.secret = "end user secret";

        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new SuccessEndUsersStubCall(mockGetEndUserResponse));

        when(mockCreateCredentials.createCredentials(
                Matchers.<CreateCredentials.Request>any()
        )).thenReturn(new SuccessCredentialStubCall(mockCredentialsResponse));

        SUT.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUsers).getEndUsers(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        );

        verify(mockCreateCredentials).createCredentials(
                Matchers.<CreateCredentials.Request>anyObject()
        );

        verify(mockCallback, never()).onFailure(
                Matchers.<Call<XivelyAccount>>any(),
                Matchers.<Throwable>any()
        );

        verify(mockCallback, times(1)).onResponse(
                Matchers.<Call<XivelyAccount>>anyObject(),
                captorXivelyAccountResponse.capture()
        );

        XivelyAccount result = captorXivelyAccountResponse.getValue().body();
        assertNotNull(result);
        assertEquals("mock account id", result.getClientId());
        assertNull(result.getDisplayName());
        assertEquals("end user id", result.getUserName());
        assertEquals("end user secret", result.getPassword());
    }

    private class ClassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassMatcher(Class<T> targetClass) {
            this.targetClass = targetClass;
        }


        @Override
        public boolean matches(Object item) {
            if (item != null) {
                return targetClass.isAssignableFrom((Class<?>) item);
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Is instance of the specified class.");
        }
    }

    private class SuccessEndUsersStubCall implements Call<GetEndUsers.Response> {

        GetEndUsers.Response response;

        public SuccessEndUsersStubCall() {
            this.response = new GetEndUsers.Response();
        }

        public SuccessEndUsersStubCall(GetEndUsers.Response response) {
            this.response = response;
        }

        @Override
        public Response<GetEndUsers.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUsers.Response> callback) {
            Response<GetEndUsers.Response> retrofitResponse = Response.success(response);
            callback.onResponse(this, retrofitResponse);
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<GetEndUsers.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureEndUsersStubCall implements Call<GetEndUsers.Response> {

        @Override
        public Response<GetEndUsers.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUsers.Response> callback) {
            callback.onFailure(this, new Throwable("Just and error message"));
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<GetEndUsers.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class SuccessAccountUserStubCall implements Call<GetAccountUser.Response> {

        GetAccountUser.Response response;

        public SuccessAccountUserStubCall() {
            this.response = new GetAccountUser.Response();
        }

        public SuccessAccountUserStubCall(GetAccountUser.Response response) {
            this.response = response;
        }

        @Override
        public Response<GetAccountUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetAccountUser.Response> callback) {
            Response<GetAccountUser.Response> retrofitResponse = Response.success(response);
            callback.onResponse(this, retrofitResponse);
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<GetAccountUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureAccountUserStubCall implements Call<GetAccountUser.Response> {

        @Override
        public Response<GetAccountUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetAccountUser.Response> callback) {
            callback.onFailure(this, new Throwable("Just and error message"));
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<GetAccountUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class SuccessCredentialStubCall implements Call<CreateCredentials.Response> {

        CreateCredentials.Response response;

        public SuccessCredentialStubCall() {
            this.response = new CreateCredentials.Response();
        }

        public SuccessCredentialStubCall(CreateCredentials.Response response) {
            this.response = response;
        }

        @Override
        public Response<CreateCredentials.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<CreateCredentials.Response> callback) {
            Response<CreateCredentials.Response> retrofitResponse = Response.success(response);
            callback.onResponse(this, retrofitResponse);
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<CreateCredentials.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureCredentialStubCall implements Call<CreateCredentials.Response> {
        @Override
        public Response<CreateCredentials.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<CreateCredentials.Response> callback) {
            callback.onFailure(this, new Throwable("Just and error message"));
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<CreateCredentials.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}