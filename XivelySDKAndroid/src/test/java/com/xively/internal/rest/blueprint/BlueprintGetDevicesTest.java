package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiDeviceInfo.ProvisioningStateEnum;

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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetDevicesTest extends TestCase {

    @Mock
    private GetDevices mockGetDevices;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDevicesCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                mockGetDevices,
                null,
                null,
                null,
                null,
                null
        );

        final String accountId = "mock account id";
        final String deviceTemplateId = "mock device template id";
        final String organizationId = "mock organization id";
        final ProvisioningStateEnum provisioningState = ProvisioningStateEnum.activated;
        final int page = 66;
        final int pageSize = 99;

        when(mockGetDevices.getDevices(anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(new SuccessStubCall());

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

        verify(mockGetDevices, times(1)).getDevices(
                eq(accountId),
                eq(deviceTemplateId),
                eq(organizationId),
                eq(provisioningState.toString()),
                eq(Boolean.TRUE),
                eq(Boolean.TRUE),
                eq(page),
                eq(pageSize),
                anyString(),
                anyString()
        );
    }

    private class SuccessStubCall implements Call<GetDevices.Response> {

        @Override
        public Response<GetDevices.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetDevices.Response> callback) {
            GetDevices.Response response = new GetDevices.Response();
            Response<GetDevices.Response> retrofitResponse = Response.success(response);
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
        public Call<GetDevices.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<GetDevices.Response> {

        @Override
        public Response<GetDevices.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetDevices.Response> callback) {
            callback.onFailure(this, new Throwable("Just and error message"));
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
        public Call<GetDevices.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}