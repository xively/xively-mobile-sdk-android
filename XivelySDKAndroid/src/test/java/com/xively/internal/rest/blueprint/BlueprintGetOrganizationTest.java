package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Callback;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class BlueprintGetOrganizationTest extends TestCase {

    @Mock
    private GetOrganization mockGetOrganization;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices();

        Callback<GetOrganization.Response> mockCallback = mock(Callback.class);

        final String organizationId = "mock orgid";
        SUT.getOrganization(organizationId, mockCallback);

        verify(mockGetOrganization, times(1)).getOrganization(eq(organizationId));
    }
}
