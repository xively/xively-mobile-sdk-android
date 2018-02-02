package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.junit.Test;
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

    @Test
    public void testGetOrganizationsCallsServiceSuccess() throws Exception {
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

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockGetOrganizations.getOrganizations(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        )).thenReturn(successStubCall);

        SUT.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId, page, pageSize, new Callback<GetOrganizations.Response>() {
            @Override
            public void onResponse(Call<GetOrganizations.Response> call, Response<GetOrganizations.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<GetOrganizations.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockGetOrganizations, times(1)).getOrganizations(
                anyString(),
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

    @Test
    public void testGetOrganizationsCallsServiceFailure() throws Exception {
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

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockGetOrganizations.getOrganizations(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        )).thenReturn(failureStubCall);

        SUT.getOrganizations(accountId, parentId, deviceTemplateId, organizationTemplateId, page, pageSize, new Callback<GetOrganizations.Response>() {
            @Override
            public void onResponse(Call<GetOrganizations.Response> call, Response<GetOrganizations.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<GetOrganizations.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockGetOrganizations, times(1)).getOrganizations(
                anyString(),
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

    private class SuccessStubCall implements Call<GetOrganizations.Response> {
        @Override
        public Response<GetOrganizations.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetOrganizations.Response> callback) {
            GetOrganizations.Response response = new GetOrganizations.Response();
            Response<GetOrganizations.Response> retrofitResponse = Response.success(response);
            callback.onResponse(this, retrofitResponse);
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

    private class FailureStubCall implements Call<GetOrganizations.Response> {
        @Override
        public Response<GetOrganizations.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetOrganizations.Response> callback) {
            callback.onFailure(this, new Throwable("Just an exception"));
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
