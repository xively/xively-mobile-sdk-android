package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by milgra on 27/07/16.
 */
public class BlueprintGetOrganizationTest extends TestCase {
    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<GetOrganization.Response> mockCallback = mock(Callback.class);
        GetOrganization mockGetOrganization = mock(GetOrganization.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetOrganization);

        final String organizationId = "mock orgid";
        testWS.getOrganization( organizationId, mockCallback );

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockGetOrganization, timeout(500).times(1)).getOrganization(eq(organizationId),eq(mockCallback));

    }
}
