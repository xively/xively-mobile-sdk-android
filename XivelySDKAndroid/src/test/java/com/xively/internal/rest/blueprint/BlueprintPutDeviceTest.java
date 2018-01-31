package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

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

    public void testPutDeviceCallsService() throws Exception {
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

        when(mockPutDevice.putDevice(anyString(), anyString(), Matchers.<HashMap<String, Object>>any())).thenReturn(new SuccessStubCall());

        SUT.putDevice(deviceId, version, data, new Callback<PutDevice.Response>() {
            @Override
            public void onResponse(Call<PutDevice.Response> call, Response<PutDevice.Response> response) {

            }

            @Override
            public void onFailure(Call<PutDevice.Response> call, Throwable t) {

            }
        });

        verify(mockPutDevice, times(1)).putDevice(eq(deviceId), eq(version), eq(data));
    }

    private class SuccessStubCall implements Call<PutDevice.Response> {

        @Override
        public Response<PutDevice.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutDevice.Response> callback) {

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