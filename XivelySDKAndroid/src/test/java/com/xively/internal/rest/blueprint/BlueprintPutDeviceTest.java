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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintPutDeviceTest extends TestCase {

    @Mock
    private PutDevice mockPutDevice;

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
                mockPutDevice,
                null,
                null,
                null,
                null,
                null,
                null
        );

        final String deviceId = "mock account id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("connected", "true");

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockPutDevice.putDevice(
                anyString(),
                anyString(),
                anyString(),
                Matchers.<HashMap<String, Object>>any()
        )).thenReturn(successStubCall);

        SUT.putDevice(deviceId, version, data, new Callback<PutDevice.Response>() {
            @Override
            public void onResponse(Call<PutDevice.Response> call, Response<PutDevice.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<PutDevice.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockPutDevice, times(1)).putDevice(
                anyString(),
                eq(deviceId),
                eq(version),
                eq(data)
        );
    }

    @Test
    public void testPutDeviceCallsServiceFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                mockPutDevice,
                null,
                null,
                null,
                null,
                null,
                null
        );

        final String deviceId = "mock account id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("connected", "true");

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockPutDevice.putDevice(
                anyString(),
                anyString(),
                anyString(),
                Matchers.<HashMap<String, Object>>any()
        )).thenReturn(failureStubCall);

        SUT.putDevice(deviceId, version, data, new Callback<PutDevice.Response>() {
            @Override
            public void onResponse(Call<PutDevice.Response> call, Response<PutDevice.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<PutDevice.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockPutDevice, times(1)).putDevice(
                anyString(),
                eq(deviceId),
                eq(version),
                eq(data)
        );
    }

    private class SuccessStubCall implements Call<PutDevice.Response> {

        @Override
        public Response<PutDevice.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutDevice.Response> callback) {
            PutDevice.Response response = new PutDevice.Response();
            Response<PutDevice.Response> retrofitResponse = Response.success(response);
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
        public Call<PutDevice.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<PutDevice.Response> {

        @Override
        public Response<PutDevice.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutDevice.Response> callback) {
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
        public Call<PutDevice.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}