package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BlueprintGetDeviceTest extends TestCase {

    @Mock
    private GetDevice mockGetDevice;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDeviceCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        final String deviceId = "mock account id";
        testWS.getDevice(deviceId, new Callback<GetDevice.Response>() {
            @Override
            public void onResponse(Call<GetDevice.Response> call, Response<GetDevice.Response> response) {

            }

            @Override
            public void onFailure(Call<GetDevice.Response> call, Throwable t) {

            }
        });

        verify(mockGetDevice, times(1)).getDevice(eq(deviceId));
    }
}
