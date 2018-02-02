package com.xively.internal.rest.provision;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.provision.StartAssociationWithCode;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisionWebServicesTest extends TestCase {

    private Callback<StartAssociationWithCode.Response> callback;

    @Mock
    private StartAssociationWithCode associationApi;
    @Captor
    private ArgumentCaptor<StartAssociationWithCode.Request> captorAssociationRequest;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartAssociationWithCodeSuccess() throws Exception {
        ProvisionWebServices SUT = new ProvisionWebServices(associationApi);

        String userId = "mock user Id";
        String code = "mock association code";
        String authHeader = "myAuthHeader";

        final SuccessStubCall successStubCall = new SuccessStubCall();
        callback = new Callback<StartAssociationWithCode.Response>() {
            @Override
            public void onResponse(Call<StartAssociationWithCode.Response> call, Response<StartAssociationWithCode.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(200, response.code());
            }

            @Override
            public void onFailure(Call<StartAssociationWithCode.Response> call, Throwable t) {
                fail();
            }
        };

        when(associationApi.startAssociationWithCode(anyString(), any(StartAssociationWithCode.Request.class))).thenReturn(successStubCall);

        SUT.setBearerAuthorizationHeader(authHeader);
        SUT.associateIoTDevice(code, userId, callback);

        verify(associationApi, times(1)).startAssociationWithCode(eq("Bearer " + authHeader), captorAssociationRequest.capture());

        StartAssociationWithCode.Request associationRequest = captorAssociationRequest.getValue();

        assertNotNull(associationRequest);
        assertEquals(associationRequest.endUserId, userId);
        assertEquals(associationRequest.associationCode, code);
    }

    @Test
    public void testStartAssociationWithCodeFailure() throws Exception {
        ProvisionWebServices SUT = new ProvisionWebServices(associationApi);

        String userId = "mock user Id";
        String code = "mock association code";
        String authHeader = "Bearer myAuthHeader";

        final FailureStubCall failureStubCall = new FailureStubCall();
        callback = new Callback<StartAssociationWithCode.Response>() {
            @Override
            public void onResponse(Call<StartAssociationWithCode.Response> call, Response<StartAssociationWithCode.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<StartAssociationWithCode.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        };

        when(associationApi.startAssociationWithCode(anyString(), any(StartAssociationWithCode.Request.class))).thenReturn(failureStubCall);

        SUT.setBearerAuthorizationHeader(authHeader);
        SUT.associateIoTDevice(code, userId, callback);

        verify(associationApi, times(1)).startAssociationWithCode(eq("Bearer " + authHeader), captorAssociationRequest.capture());

        StartAssociationWithCode.Request associationRequest = captorAssociationRequest.getValue();

        assertNotNull(associationRequest);
        assertEquals(associationRequest.endUserId, userId);
        assertEquals(associationRequest.associationCode, code);
    }

    private class SuccessStubCall implements Call<StartAssociationWithCode.Response> {

        @Override
        public Response<StartAssociationWithCode.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<StartAssociationWithCode.Response> callback) {
            StartAssociationWithCode.Response response = new StartAssociationWithCode.Response();
            Response<StartAssociationWithCode.Response> retrofitResponse = Response.success(response);
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
        public Call<StartAssociationWithCode.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<StartAssociationWithCode.Response> {

        @Override
        public Response<StartAssociationWithCode.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<StartAssociationWithCode.Response> callback) {
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
        public Call<StartAssociationWithCode.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}