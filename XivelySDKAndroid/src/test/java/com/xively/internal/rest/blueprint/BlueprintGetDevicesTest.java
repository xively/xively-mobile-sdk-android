package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiDeviceInfo.ProvisioningStateEnum;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetDevicesTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDevicesCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<GetDevices.Response> mockCallback = mock(Callback.class);
        GetDevices mockGetDevices = mock(GetDevices.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetDevices);

        final String accountId = "mock account id";
        final String deviceTemplateId = "mock device template id";
        final String organizationId = "mock organization id";
        final ProvisioningStateEnum provisioningState = ProvisioningStateEnum.activated;
        final int page = 66;
        final int pageSize = 99;

        testWS.getDevices(
                accountId, deviceTemplateId, organizationId, provisioningState, page, pageSize,
                mockCallback
        );

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockGetDevices, timeout(500).times(1)).getDevices(
                eq(accountId), eq(deviceTemplateId), eq(organizationId),
                eq(provisioningState.toString()), eq(Boolean.TRUE), eq(Boolean.TRUE),
                eq(page), eq(pageSize), anyString(), anyString(),
                eq(mockCallback)
        );

    }

}