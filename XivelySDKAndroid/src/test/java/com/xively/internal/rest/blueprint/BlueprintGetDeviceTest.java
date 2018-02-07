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


public class BlueprintGetDeviceTest extends TestCase {

    @Mock
    private GetDevice mockGetDevice;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDeviceCallsServiceSuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                mockGetDevice,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        final String deviceId = "mock account id";

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockGetDevice.getDevice(anyString(), anyString())).thenReturn(successStubCall);

        SUT.getDevice(deviceId, new Callback<GetDevice.Response>() {
            @Override
            public void onResponse(Call<GetDevice.Response> call, Response<GetDevice.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<GetDevice.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockGetDevice, times(1)).getDevice(anyString(), eq(deviceId));
    }

    @Test
    public void testGetDeviceCallsServiceFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                mockGetDevice,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        final String deviceId = "mock account id";


        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockGetDevice.getDevice(anyString(), anyString())).thenReturn(failureStubCall);

        SUT.getDevice(deviceId, new Callback<GetDevice.Response>() {
            @Override
            public void onResponse(Call<GetDevice.Response> call, Response<GetDevice.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<GetDevice.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockGetDevice, times(1)).getDevice(anyString(), eq(deviceId));
    }

    private class SuccessStubCall implements Call<GetDevice.Response> {
        @Override
        public Response<GetDevice.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetDevice.Response> callback) {
            GetDevice.Response response = new GetDevice.Response();
            Response<GetDevice.Response> retrofitResponse = Response.success(response);
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
        public Call<GetDevice.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<GetDevice.Response> {
        @Override
        public Response<GetDevice.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetDevice.Response> callback) {
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
        public Call<GetDevice.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
