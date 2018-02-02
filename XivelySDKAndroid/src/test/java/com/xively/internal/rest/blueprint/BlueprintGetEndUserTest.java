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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintGetEndUserTest extends TestCase {
    @Mock
    private GetEndUser mockGetEndUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEndUserCallsServiceSuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                mockGetEndUser,
                null,
                null,
                null,
                null
        );

        final String userId = "mock user id";

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockGetEndUser.getEndUser(anyString(), anyString())).thenReturn(successStubCall);

        SUT.getEndUser(userId, new Callback<GetEndUser.Response>() {
            @Override
            public void onResponse(Call<GetEndUser.Response> call, Response<GetEndUser.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<GetEndUser.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockGetEndUser, times(1)).getEndUser(anyString(), eq(userId));
    }

    @Test
    public void testGetEndUserCallsServiceFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                mockGetEndUser,
                null,
                null,
                null,
                null
        );

        final String userId = "mock user id";

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockGetEndUser.getEndUser(anyString(), anyString())).thenReturn(failureStubCall);

        SUT.getEndUser(userId, new Callback<GetEndUser.Response>() {
            @Override
            public void onResponse(Call<GetEndUser.Response> call, Response<GetEndUser.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<GetEndUser.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockGetEndUser, times(1)).getEndUser(anyString(), eq(userId));
    }

    private class SuccessStubCall implements Call<GetEndUser.Response> {
        @Override
        public Response<GetEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUser.Response> callback) {
            GetEndUser.Response response = new GetEndUser.Response();
            Response<GetEndUser.Response> retrofitResponse = Response.success(response);
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
        public Call<GetEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<GetEndUser.Response> {
        @Override
        public Response<GetEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUser.Response> callback) {
            callback.onFailure(this, new Throwable("Just and exception"));
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
        public Call<GetEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
