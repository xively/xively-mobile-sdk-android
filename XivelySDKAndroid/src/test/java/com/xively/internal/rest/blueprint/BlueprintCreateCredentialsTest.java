package com.xively.internal.rest.blueprint;


import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintCreateCredentialsTest extends TestCase {
    @Mock
    private CreateCredentials mockCreateCredentials;
    @Captor
    private ArgumentCaptor<CreateCredentials.Request> captorCreateCredentialsRequest;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartCreateCredentialsQuerySuccess() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        String accountId = "mock account id";
        String userId = "mock access user id";

        final SuccessStubCall successStubCall = new SuccessStubCall();
        when(mockCreateCredentials.createCredentials(
                anyString(),
                any(CreateCredentials.Request.class)
        )).thenReturn(successStubCall);

        SUT.createCredentials(accountId, userId, BlueprintWebServices.BluePrintEntity.endUser, new Callback<CreateCredentials.Response>() {
            @Override
            public void onResponse(Call<CreateCredentials.Response> call, Response<CreateCredentials.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(response.code(), 200);
            }

            @Override
            public void onFailure(Call<CreateCredentials.Response> call, Throwable t) {
                fail();
            }
        });

        verify(mockCreateCredentials, times(1)).createCredentials(
                anyString(),
                captorCreateCredentialsRequest.capture()
        );

        CreateCredentials.Request request = captorCreateCredentialsRequest.getValue();

        assertNotNull(request);
        assertEquals(accountId, request.accountId);
        assertEquals(userId, request.entityId);
        assertEquals("endUser", request.entityType);
    }

    @Test
    public void testStartCreateCredentialsQueryFailure() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                mockCreateCredentials,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        String accountId = "mock account id";
        String userId = "mock access user id";

        final FailureStubCall failureStubCall = new FailureStubCall();
        when(mockCreateCredentials.createCredentials(
                anyString(),
                any(CreateCredentials.Request.class)
        )).thenReturn(failureStubCall);

        SUT.createCredentials(accountId, userId, BlueprintWebServices.BluePrintEntity.endUser, new Callback<CreateCredentials.Response>() {
            @Override
            public void onResponse(Call<CreateCredentials.Response> call, Response<CreateCredentials.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<CreateCredentials.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        });

        verify(mockCreateCredentials, times(1)).createCredentials(
                anyString(),
                captorCreateCredentialsRequest.capture()
        );

        CreateCredentials.Request request = captorCreateCredentialsRequest.getValue();

        assertNotNull(request);
        assertEquals(accountId, request.accountId);
        assertEquals(userId, request.entityId);
        assertEquals("endUser", request.entityType);
    }

    private class SuccessStubCall implements Call<CreateCredentials.Response> {
        @Override
        public Response<CreateCredentials.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<CreateCredentials.Response> callback) {
            CreateCredentials.Response response = new CreateCredentials.Response();
            Response<CreateCredentials.Response> retrofitResponse = Response.success(response);
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
        public Call<CreateCredentials.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<CreateCredentials.Response> {
        @Override
        public Response<CreateCredentials.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<CreateCredentials.Response> callback) {
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
        public Call<CreateCredentials.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
