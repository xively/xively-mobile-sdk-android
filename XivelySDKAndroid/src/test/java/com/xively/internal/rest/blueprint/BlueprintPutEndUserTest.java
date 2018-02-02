package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintPutEndUserTest extends TestCase {

    @Mock
    private PutEndUser mockPutEndUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPutDeviceCallsServiceSuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                mockPutEndUser,
                null,
                null,
                null
        );

        final String userId = "mock user id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("emailAddress", "anotheremail");

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockPutEndUser.putEndUser(
                anyString(),
                anyString(),
                anyString(),
                Matchers.<HashMap<String, Object>>any()
        )).thenReturn(successStubCall);

        SUT.putEndUser(userId, version, data, new Callback<PutEndUser.Response>() {
            @Override
            public void onResponse(Call<PutEndUser.Response> call, Response<PutEndUser.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<PutEndUser.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockPutEndUser, timeout(500).times(1)).putEndUser(anyString(), eq(userId), eq(version), eq(data));
    }

    @Test
    public void testPutDeviceCallsServiceFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                mockPutEndUser,
                null,
                null,
                null
        );

        final String userId = "mock user id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("emailAddress", "anotheremail");

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockPutEndUser.putEndUser(
                anyString(),
                anyString(),
                anyString(),
                Matchers.<HashMap<String, Object>>any()
        )).thenReturn(failureStubCall);

        SUT.putEndUser(userId, version, data, new Callback<PutEndUser.Response>() {
            @Override
            public void onResponse(Call<PutEndUser.Response> call, Response<PutEndUser.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<PutEndUser.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockPutEndUser, timeout(500).times(1)).putEndUser(anyString(), eq(userId), eq(version), eq(data));
    }

    private class SuccessStubCall implements Call<PutEndUser.Response> {

        @Override
        public Response<PutEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutEndUser.Response> callback) {
            PutEndUser.Response response = new PutEndUser.Response();
            Response<PutEndUser.Response> retrofitResponse = Response.success(response);
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
        public Call<PutEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<PutEndUser.Response> {

        @Override
        public Response<PutEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutEndUser.Response> callback) {
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
        public Call<PutEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
