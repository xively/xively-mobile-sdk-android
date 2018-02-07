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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintGetUsersTest extends TestCase {

    @Mock
    private GetEndUsers mockGetEndUsers;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartGetEndUserQuerySuccess() throws Exception {
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

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(successStubCall);

        SUT.getEndUsers(accountId, userId, new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(Call<GetEndUsers.Response> call, Response<GetEndUsers.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {
                fail();
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
    public void testStartGetEndUserQueryFailure() throws Exception {
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

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockGetEndUsers.getEndUsers(
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(failureStubCall);

        SUT.getEndUsers(accountId, userId, new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(Call<GetEndUsers.Response> call, Response<GetEndUsers.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
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

    private class SuccessStubCall implements Call<GetEndUsers.Response> {
        @Override
        public Response<GetEndUsers.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUsers.Response> callback) {
            GetEndUsers.Response response = new GetEndUsers.Response();
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

    private class FailureStubCall implements Call<GetEndUsers.Response> {
        @Override
        public Response<GetEndUsers.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUsers.Response> callback) {
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
        public Call<GetEndUsers.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}