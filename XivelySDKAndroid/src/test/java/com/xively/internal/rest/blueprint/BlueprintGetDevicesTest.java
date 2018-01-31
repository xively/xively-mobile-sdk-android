package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiDeviceInfo.ProvisioningStateEnum;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class BlueprintGetDevicesTest extends TestCase {

    @Mock
    private GetDevices mockGetDevices;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDevicesCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices();

        final String accountId = "mock account id";
        final String deviceTemplateId = "mock device template id";
        final String organizationId = "mock organization id";
        final ProvisioningStateEnum provisioningState = ProvisioningStateEnum.activated;
        final int page = 66;
        final int pageSize = 99;

        SUT.getDevices(
                accountId, deviceTemplateId, organizationId, provisioningState, page, pageSize,
                new Callback<GetDevices.Response>() {
                    @Override
                    public void onResponse(Call<GetDevices.Response> call, Response<GetDevices.Response> response) {

                    }

                    @Override
                    public void onFailure(Call<GetDevices.Response> call, Throwable t) {

                    }
                }
        );

        verify(mockGetDevices, timeout(500).times(1)).getDevices(
                eq(accountId), eq(deviceTemplateId), eq(organizationId),
                eq(provisioningState.toString()), eq(Boolean.TRUE), eq(Boolean.TRUE),
                eq(page), eq(pageSize), anyString(), anyString()
        );
    }
}