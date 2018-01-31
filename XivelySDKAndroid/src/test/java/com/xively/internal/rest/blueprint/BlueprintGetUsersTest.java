package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BlueprintGetUsersTest extends TestCase {

    @Mock
    private GetEndUsers mockGetEndUser;
    @Mock
    private GetAccountUser mockGetAccountUser;
    @Mock
    private CreateCredentials mockCreateCredentials;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testStartGetEndUserQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();


        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.getEndUsers(accountId, userId, new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(Call<GetEndUsers.Response> call, Response<GetEndUsers.Response> response) {

            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {

            }
        });

        verify(mockGetEndUser, times(1)).getEndUsers(
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString()
        );
    }

    public void testStartGeAccountUserQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.getAccountUser(accountId, userId, new Callback<GetAccountUser.Response>() {
            @Override
            public void onResponse(Call<GetAccountUser.Response> call, Response<GetAccountUser.Response> response) {

            }

            @Override
            public void onFailure(Call<GetAccountUser.Response> call, Throwable t) {

            }
        });

        verify(mockGetAccountUser, timeout(1000).times(1)).getAccountUser(
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString()
        );
    }

    public void testStartCreateCredentialsQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        ArgumentCaptor<CreateCredentials.Request> createCredentialsRequestCaptor =
                ArgumentCaptor.forClass(CreateCredentials.Request.class);

        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.createCredentials(accountId, userId, BlueprintWebServices.BluePrintEntity.endUser, new Callback<CreateCredentials.Response>() {
            @Override
            public void onResponse(Call<CreateCredentials.Response> call, Response<CreateCredentials.Response> response) {

            }

            @Override
            public void onFailure(Call<CreateCredentials.Response> call, Throwable t) {

            }
        });

        verify(mockCreateCredentials, timeout(1000).times(1)).createCredentials(
                createCredentialsRequestCaptor.capture()
        );

        CreateCredentials.Request request = createCredentialsRequestCaptor.getValue();
        assertNotNull(request);
        assertEquals(accountId, request.accountId);
        assertEquals(userId, request.entityId);
        assertEquals("endUser", request.entityType);
    }

}