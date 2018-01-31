package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetOrganizationsTest extends TestCase {

    @Mock
    private GetOrganizations mockGetOrganizations;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationsCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mockGetOrganizations
        );

        final String accountId = "mock account id";
        final String parentId = "mock device parent id";
        final String deviceTemplateId = "mock device template id";
        final String organizationTemplateId = "mock organization template id";
        final int page = 66;
        final int pageSize = 99;

        when(mockGetOrganizations.getOrganizations(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(new SuccsssStubCall());

        SUT.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId, page, pageSize, new Callback<GetOrganizations.Response>() {
            @Override
            public void onResponse(Call<GetOrganizations.Response> call, Response<GetOrganizations.Response> response) {

            }

            @Override
            public void onFailure(Call<GetOrganizations.Response> call, Throwable t) {

            }
        });

        verify(mockGetOrganizations, times(1)).getOrganizations(
                eq(accountId),
                eq(parentId),
                eq(deviceTemplateId),
                eq(organizationTemplateId),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                eq(page),
                eq(pageSize),
                anyString(),
                anyString()
        );
    }

    private class SuccsssStubCall implements Call<GetOrganizations.Response> {
        @Override
        public Response<GetOrganizations.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetOrganizations.Response> callback) {

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
        public Call<GetOrganizations.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
