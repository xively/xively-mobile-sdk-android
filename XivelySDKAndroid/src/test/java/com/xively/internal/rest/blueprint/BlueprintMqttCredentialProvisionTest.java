package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.XiSdkConfig;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUser;
import com.xively.internal.rest.blueprint.accountUserQuery.AccountUsersList;
import com.xively.internal.rest.blueprint.credentialsCreate.Credential;
import com.xively.internal.rest.blueprint.endUserQuery.EndUser;
import com.xively.internal.rest.blueprint.endUserQuery.EndUsersListResponse;
import com.xively.messaging.XiEndUserInfo;

import junit.framework.TestCase;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class BlueprintMqttCredentialProvisionTest extends TestCase {

    @Mock
    private RestAdapter mockRestAdapter;
    @Mock
    private Callback<XivelyAccount> mockCallback;
    @Mock
    private GetEndUsers mockGetEndUser;
    @Mock
    private GetAccountUser mockGetAccountUser;
    @Mock
    private CreateCredentials mockCreateCredentials;

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
        setUp();

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        testWS.queryXivelyAccount(null, mockCallback);
        verify(mockCallback, times(1)).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, never()).success(
                Matchers.<XivelyAccount>anyObject(),
                Matchers.<Response>anyObject());
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnInvalidJwt() throws Exception {
        setUp();

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        testWS.queryXivelyAccount("something wrong", mockCallback);
        verify(mockCallback, times(1)).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, never()).success(
                Matchers.<XivelyAccount>anyObject(),
                Matchers.<Response>anyObject());
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnCorruptJwtData() throws Exception {
        setUp();

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        testWS.queryXivelyAccount(corruptJwt, mockCallback);
        verify(mockCallback, times(1)).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, never()).success(
                Matchers.<XivelyAccount>anyObject(),
                Matchers.<Response>anyObject());
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnGetEndUserFailure() throws Exception {
        setUp();

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetEndUsers>(GetEndUsers.class))))
                .thenReturn(mockGetEndUser);
        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetAccountUser>(GetAccountUser.class))))
                .thenReturn(mockGetAccountUser);

                ArgumentCaptor < Callback > callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);
        testWS.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUser).getEndUsers(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), callbackCaptor.capture());
        assertNotNull(callbackCaptor.getValue());
        callbackCaptor.getValue().failure(null);

        verify(mockGetAccountUser, timeout(500)).getAccountUser(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), callbackCaptor.capture());
        assertNotNull(callbackCaptor.getValue());
        callbackCaptor.getValue().failure(null);

        verify(mockCallback, times(1)).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, never()).success(
                Matchers.<XivelyAccount>anyObject(),
                Matchers.<Response>anyObject());
    }

    @Test
    public void testQueryXiAccountFailureCallbackOnCreateCredentialsFailure() throws Exception {
        setUp();

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetEndUsers>(GetEndUsers.class))))
                .thenReturn(mockGetEndUser);

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<CreateCredentials>(CreateCredentials.class))))
                .thenReturn(mockCreateCredentials);

        ArgumentCaptor<Callback> getEndUserCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Callback> createCredentialsCallbackCaptor = ArgumentCaptor.forClass(Callback.class);

        GetEndUsers.Response mockGetEndUserResponse = new GetEndUsers.Response();
        mockGetEndUserResponse.endUsers = new HashMap<String,Object>();

        mockGetEndUserResponse.endUsers = new HashMap<String,Object>();

        LinkedTreeMap<String, Object> endUserMap = new LinkedTreeMap<String,Object>();
        endUserMap.put("id","mock user id");
        endUserMap.put("userId","mock user id");

        ArrayList<Object> endUserList = new ArrayList<Object>();
        endUserList.add(endUserMap);

        mockGetEndUserResponse.endUsers.put("results" , endUserList );

//        mockGetEndUserResponse.endUsers.results = new EndUser[1];
//        mockGetEndUserResponse.endUsers.results[0] = new EndUser();
//        mockGetEndUserResponse.endUsers.results[0].id = "Béla";
//        mockGetEndUserResponse.endUsers.results[0].userId
//                = "blueprint tudja minek ide prefix/mock user id";//this id must match the one from the jwt

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);
        testWS.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUser, timeout(500)).getEndUsers(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), getEndUserCallbackCaptor.capture());

        assertNotNull(getEndUserCallbackCaptor.getValue());
        getEndUserCallbackCaptor.getValue().success(mockGetEndUserResponse, null);

        verify(mockCreateCredentials, timeout(500)).createCredentials(Matchers.<CreateCredentials.Request>anyObject(), createCredentialsCallbackCaptor.capture());
        assertNotNull(createCredentialsCallbackCaptor.getValue());
        createCredentialsCallbackCaptor.getValue().failure(null);

        verify(mockCallback, times(1)).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, never()).success(
                Matchers.<XivelyAccount>anyObject(),
                Matchers.<Response>anyObject());
    }

    @Test
    public void testQueryXiEndUserAccountSuccess() throws Exception {
        setUp();

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetEndUsers>(GetEndUsers.class))))
                .thenReturn(mockGetEndUser);
        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetAccountUser>(GetAccountUser.class))))
                .thenReturn(mockGetAccountUser);
        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<CreateCredentials>(CreateCredentials.class))))
                .thenReturn(mockCreateCredentials);

        ArgumentCaptor<Callback> getEndUserCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Callback> getAccountUserCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Callback> createCredentialsCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<XivelyAccount> xivelyAccountCaptor = ArgumentCaptor.forClass(XivelyAccount.class);

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

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);
        testWS.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUser, timeout(500)).getEndUsers(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), getEndUserCallbackCaptor.capture());
        assertNotNull(getEndUserCallbackCaptor.getValue());
        getEndUserCallbackCaptor.getValue().failure(null);

        verify(mockGetAccountUser, timeout(500)).getAccountUser(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), getAccountUserCallbackCaptor.capture());
        assertNotNull(getAccountUserCallbackCaptor.getValue());
        getAccountUserCallbackCaptor.getValue().success(mockGetAccountUserResponse, null);

        verify(mockCreateCredentials, timeout(500)).createCredentials(Matchers.<CreateCredentials.Request>anyObject(), createCredentialsCallbackCaptor.capture());
        assertNotNull(createCredentialsCallbackCaptor.getValue());
        createCredentialsCallbackCaptor.getValue().success(mockCredentialsResponse, null);

        verify(mockCallback, never()).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, times(1)).success(
                xivelyAccountCaptor.capture(),
                Matchers.<Response>anyObject());

        XivelyAccount result = xivelyAccountCaptor.getValue();
        assertNotNull(result);
        assertEquals("mock account id", result.getClientId());
        assertNull(result.getDisplayName());
        assertEquals("end user id", result.getUserName());
        assertEquals("end user secret", result.getPassword());
    }

    @Test
    public void testQueryXiAccountUserSuccess() throws Exception {
        setUp();

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<GetEndUsers>(GetEndUsers.class))))
                .thenReturn(mockGetEndUser);

        when(mockRestAdapter.create(Matchers.argThat(new ClassMatcher<CreateCredentials>(CreateCredentials.class))))
                .thenReturn(mockCreateCredentials);

        ArgumentCaptor<Callback> getEndUserCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Callback> createCredentialsCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<XivelyAccount> xivelyAccountCaptor = ArgumentCaptor.forClass(XivelyAccount.class);

        GetEndUsers.Response mockGetEndUserResponse = new GetEndUsers.Response();
        mockGetEndUserResponse.endUsers = new HashMap<String,Object>();

        LinkedTreeMap<String, Object> endUserMap = new LinkedTreeMap<String,Object>();
        endUserMap.put("id","mock user id");
        endUserMap.put("userId","blueprint tudja minek ide prefix/mock user id");

        ArrayList<Object> endUserList = new ArrayList<Object>();
        endUserList.add(endUserMap);

        mockGetEndUserResponse.endUsers.put("results" , endUserList );

//        mockGetEndUserResponse.endUsers.results = new EndUser[1];
//        mockGetEndUserResponse.endUsers.results[0] = new EndUser();
//        mockGetEndUserResponse.endUsers.results[0].id = "Béla";
//        mockGetEndUserResponse.endUsers.results[0].userId
//                = "blueprint tudja minek ide prefix/mock user id";//this id must match the one from the jwt

        CreateCredentials.Response mockCredentialsResponse = new CreateCredentials.Response();
        mockCredentialsResponse.mqttCredential = new Credential();
        mockCredentialsResponse.mqttCredential.entityId = "end user id";
        mockCredentialsResponse.mqttCredential.secret = "end user secret";

        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);
        testWS.queryXivelyAccount(testJwt, mockCallback);

        verify(mockGetEndUser, timeout(500)).getEndUsers(anyString(), anyString(), anyBoolean(), anyBoolean(),
                anyInt(), anyInt(), anyString(), getEndUserCallbackCaptor.capture());

        assertNotNull(getEndUserCallbackCaptor.getValue());
        getEndUserCallbackCaptor.getValue().success(mockGetEndUserResponse, null);

        verify(mockCreateCredentials, timeout(500)).createCredentials(Matchers.<CreateCredentials.Request>anyObject(), createCredentialsCallbackCaptor.capture());
        assertNotNull(createCredentialsCallbackCaptor.getValue());
        createCredentialsCallbackCaptor.getValue().success(mockCredentialsResponse, null);

        verify(mockCallback, never()).failure(Matchers.<RetrofitError>any());
        verify(mockCallback, times(1)).success(
                xivelyAccountCaptor.capture(),
                Matchers.<Response>anyObject());

        XivelyAccount result = xivelyAccountCaptor.getValue();
        assertNotNull(result);
        assertEquals("mock account id", result.getClientId());
        assertNull(result.getDisplayName());
        assertEquals("end user id", result.getUserName());
        assertEquals("end user secret", result.getPassword());
    }

    private class ClassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassMatcher(Class<T> targetClass){
            this.targetClass = targetClass;
        }


        @Override
        public boolean matches(Object item) {
            if (item != null){
                return targetClass.isAssignableFrom((Class<?>) item);
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Is instance of the specified class.");
        }
    }
}