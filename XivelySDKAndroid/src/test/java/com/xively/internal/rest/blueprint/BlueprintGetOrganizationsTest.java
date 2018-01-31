package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class BlueprintGetOrganizationsTest extends TestCase {

    @Mock
    private GetOrganizations mockGetOrganizations;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationsCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        final String accountId = "mock account id";
        final String parentId = "mock device parent id";
        final String deviceTemplateId = "mock device template id";
        final String organizationTemplateId = "mock organization template id";
        final int page = 66;
        final int pageSize = 99;

        testWS.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId, page, pageSize, new Callback<GetOrganizations.Response>() {
            @Override
            public void onResponse(Call<GetOrganizations.Response> call, Response<GetOrganizations.Response> response) {

            }

            @Override
            public void onFailure(Call<GetOrganizations.Response> call, Throwable t) {

            }
        });

        verify(mockGetOrganizations, timeout(500).times(1)).getOrganizations(
                eq(accountId), eq(parentId), eq(deviceTemplateId), eq(organizationTemplateId),
                anyString(), anyBoolean(), anyBoolean(),
                eq(page), eq(pageSize), anyString(), anyString()
        );
    }
}
