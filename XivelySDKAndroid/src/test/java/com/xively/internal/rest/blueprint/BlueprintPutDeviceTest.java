package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.blueprint.devicesQuery.XiDeviceData;
import com.xively.messaging.XiDeviceUpdateInfo;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintPutDeviceTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testPutDeviceCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<PutDevice.Response> mockCallback = mock(Callback.class);
        PutDevice mockPutDevice = mock(PutDevice.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockPutDevice);

        final String deviceId = "mock account id";
        final String version = "ef";
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("connected","true");
        testWS.putDevice( deviceId , version, data, mockCallback );

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockPutDevice, timeout(500).times(1)).putDevice(eq(deviceId),eq(version),eq(data), eq(mockCallback));

    }

}