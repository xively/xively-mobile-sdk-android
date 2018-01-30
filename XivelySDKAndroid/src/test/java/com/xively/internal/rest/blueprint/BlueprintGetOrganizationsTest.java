package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Callback;
import retrofit2.Retrofit;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetOrganizationsTest extends TestCase {

    @Mock
    Retrofit mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationsCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<GetOrganizations.Response> mockCallback = mock(Callback.class);
        GetOrganizations mockGetOrganizations = mock(GetOrganizations.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetOrganizations);

        final String accountId = "mock account id";
        final String parentId = "mock device parent id";
        final String deviceTemplateId = "mock device template id";
        final String organizationTemplateId = "mock organization template id";
        final int page = 66;
        final int pageSize = 99;

        testWS.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId, page, pageSize, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockGetOrganizations, timeout(500).times(1)).getOrganizations(
                eq(accountId), eq(parentId), eq(deviceTemplateId), eq(organizationTemplateId),
                anyString(), anyBoolean(), anyBoolean(),
                eq(page), eq(pageSize), anyString(), anyString(),
                eq(mockCallback)
        );
    }
}
