package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintGetUsersTest extends TestCase {

    @Mock
    private GetEndUsers mockGetEndUsers;
    @Mock
    private GetAccountUser mockGetAccountUser;
    @Mock
    private CreateCredentials mockCreateCredentials;
    @Captor
    private ArgumentCaptor<CreateCredentials.Request> captorCreateCredentialsRequest;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartGetEndUserQuery() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mockGetEndUsers,
                null,
                null
        );


        String accountId = "mock account id";
        String userId = "mock access user id";

        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new SuccessStubCall<GetEndUsers.Response>());

        SUT.getEndUsers(accountId, userId, new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(Call<GetEndUsers.Response> call, Response<GetEndUsers.Response> response) {

            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {

            }
        });

        verify(mockGetEndUsers, times(1)).getEndUsers(
                anyString(),
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString()
        );
    }

    @Test
    public void testStartGeAccountUserQuery() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                mockGetAccountUser,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        String accountId = "mock account id";
        String userId = "mock access user id";

        when(mockGetAccountUser.getAccountUser(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(new SuccessStubCall<GetAccountUser.Response>());

        SUT.getAccountUser(accountId, userId, new Callback<GetAccountUser.Response>() {
            @Override
            public void onResponse(Call<GetAccountUser.Response> call, Response<GetAccountUser.Response> response) {

            }

            @Override
            public void onFailure(Call<GetAccountUser.Response> call, Throwable t) {

            }
        });

        verify(mockGetAccountUser, times(1)).getAccountUser(
                anyString(),
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString()
        );
    }

    @Test
    public void testStartCreateCredentialsQuery() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        String accountId = "mock account id";
        String userId = "mock access user id";

        when(mockCreateCredentials.createCredentials(
                anyString(),
                any(CreateCredentials.Request.class)
        )).thenReturn(new SuccessStubCall<CreateCredentials.Response>());

        SUT.createCredentials(accountId, userId, BlueprintWebServices.BluePrintEntity.endUser, new Callback<CreateCredentials.Response>() {
            @Override
            public void onResponse(Call<CreateCredentials.Response> call, Response<CreateCredentials.Response> response) {

            }

            @Override
            public void onFailure(Call<CreateCredentials.Response> call, Throwable t) {

            }
        });

        verify(mockCreateCredentials, times(1)).createCredentials(
                anyString(),
                captorCreateCredentialsRequest.capture()
        );

        CreateCredentials.Request request = captorCreateCredentialsRequest.getValue();

        assertNotNull(request);
        assertEquals(accountId, request.accountId);
        assertEquals(userId, request.entityId);
        assertEquals("endUser", request.entityType);
    }

    private class SuccessStubCall<T> implements Call<T> {

        @Override
        public Response<T> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<T> callback) {

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
        public Call<T> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}