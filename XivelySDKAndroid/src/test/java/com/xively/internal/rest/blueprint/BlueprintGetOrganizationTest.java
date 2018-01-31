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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintGetOrganizationTest extends TestCase {

    @Mock
    private GetOrganization mockGetOrganization;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetOrganizationCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mockGetOrganization,
                null
        );

        final String organizationId = "mock orgid";

        when(mockGetOrganization.getOrganization(anyString())).thenReturn(new SuccessStubCall());

        SUT.getOrganization(organizationId, new Callback<GetOrganization.Response>() {
            @Override
            public void onResponse(Call<GetOrganization.Response> call, Response<GetOrganization.Response> response) {

            }

            @Override
            public void onFailure(Call<GetOrganization.Response> call, Throwable t) {

            }
        });

        verify(mockGetOrganization, times(1)).getOrganization(eq(organizationId));
    }

    private class SuccessStubCall implements Call<GetOrganization.Response> {
        @Override
        public Response<GetOrganization.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetOrganization.Response> callback) {

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
        public Call<GetOrganization.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
