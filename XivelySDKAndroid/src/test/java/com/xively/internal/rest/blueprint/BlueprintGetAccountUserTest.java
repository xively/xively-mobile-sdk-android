package com.xively.internal.rest.blueprint;


import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintGetAccountUserTest extends TestCase {

    @Mock
    private GetAccountUser mockGetAccountUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartGeAccountUserQuerySuccess() throws Exception {
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

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockGetAccountUser.getAccountUser(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(successStubCall);

        SUT.getAccountUser(accountId, userId, new Callback<GetAccountUser.Response>() {
            @Override
            public void onResponse(Call<GetAccountUser.Response> call, Response<GetAccountUser.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<GetAccountUser.Response> call, Throwable t) {
                fail();
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
    public void testStartGeAccountUserQueryFailure() throws Exception {
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

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockGetAccountUser.getAccountUser(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(failureStubCall);

        SUT.getAccountUser(accountId, userId, new Callback<GetAccountUser.Response>() {
            @Override
            public void onResponse(Call<GetAccountUser.Response> call, Response<GetAccountUser.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<GetAccountUser.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
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

    private class SuccessStubCall implements Call<GetAccountUser.Response> {

        @Override
        public Response<GetAccountUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetAccountUser.Response> callback) {
            GetAccountUser.Response response = new GetAccountUser.Response();
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

    private class FailureStubCall implements Call<GetAccountUser.Response> {

        @Override
        public Response<GetAccountUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetAccountUser.Response> callback) {
            callback.onFailure(this, new Throwable("Just an exception"));
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
}
