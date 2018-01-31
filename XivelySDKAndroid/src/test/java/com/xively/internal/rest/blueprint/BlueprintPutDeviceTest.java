package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BlueprintPutDeviceTest extends TestCase {

    @Mock
    private PutDevice mockPutDevice;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testPutDeviceCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        final String deviceId = "mock account id";
        final String version = "ef";
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("connected", "true");
        testWS.putDevice(deviceId, version, data, new Callback<PutDevice.Response>() {
            @Override
            public void onResponse(Call<PutDevice.Response> call, Response<PutDevice.Response> response) {

            }

            @Override
            public void onFailure(Call<PutDevice.Response> call, Throwable t) {

            }
        });

        verify(mockPutDevice, times(1)).putDevice(eq(deviceId), eq(version), eq(data));

    }

}