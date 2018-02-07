package com.xively.internal.rest.auth;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.auth.LoginUser;

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
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthWebServicesTest extends TestCase {

    private Callback<LoginUser.Response> callback;

    @Mock
    private LoginUser mockLoginApi;
    @Captor
    private ArgumentCaptor<LoginUser.Request> captorLoginRequest;
    private String JWTString = "asdasdadsda";

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginUserSuccess() throws Exception {
        AuthWebServices SUT = new AuthWebServices(mockLoginApi);

        String username = "mock username";
        String password = "mock password 123@";
        String accountId = "mock account Id";

        final SuccessStubCall successStubCall = new SuccessStubCall();
        callback = new Callback<LoginUser.Response>() {
            @Override
            public void onResponse(Call<LoginUser.Response> call, Response<LoginUser.Response> response) {
                assertEquals(successStubCall, call);
                assertEquals(200, response.code());
                assertEquals(response.body().jwt, JWTString);
            }

            @Override
            public void onFailure(Call<LoginUser.Response> call, Throwable t) {
                fail();
            }
        };

        when(mockLoginApi.loginUser(any(LoginUser.Request.class))).thenReturn(successStubCall);

        SUT.loginUser(username, password, accountId, callback);

        verify(mockLoginApi, timeout(100).times(1)).loginUser(captorLoginRequest.capture());

        LoginUser.Request loginRequest = captorLoginRequest.getValue();

        assertNotNull(loginRequest);
        assertEquals(username, loginRequest.emailAddress);
        assertEquals(password, loginRequest.password);
        assertEquals(accountId, loginRequest.accountId);
    }

    @Test
    public void testLoginUserFailure() throws Exception {
        AuthWebServices SUT = new AuthWebServices(mockLoginApi);

        String username = "mock username";
        String password = "mock password 123@";
        String accountId = "mock account Id";

        final FailureStubCall failureStubCall = new FailureStubCall();
        callback = new Callback<LoginUser.Response>() {
            @Override
            public void onResponse(Call<LoginUser.Response> call, Response<LoginUser.Response> response) {
                fail();
            }

            @Override
            public void onFailure(Call<LoginUser.Response> call, Throwable t) {
                assertEquals(failureStubCall, call);
                assertNotNull(t);
            }
        };

        when(mockLoginApi.loginUser(any(LoginUser.Request.class))).thenReturn(failureStubCall);

        SUT.loginUser(username, password, accountId, callback);

        verify(mockLoginApi, timeout(100).times(1)).loginUser(captorLoginRequest.capture());

        LoginUser.Request loginRequest = captorLoginRequest.getValue();

        assertNotNull(loginRequest);
        assertEquals(username, loginRequest.emailAddress);
        assertEquals(password, loginRequest.password);
        assertEquals(accountId, loginRequest.accountId);
    }

    private class SuccessStubCall implements Call<LoginUser.Response> {

        @Override
        public Response<LoginUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<LoginUser.Response> callback) {
            LoginUser.Response response = new LoginUser.Response();
            response.jwt = JWTString;
            Response<LoginUser.Response> retrofitResponse = Response.success(response);
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
        public Call<LoginUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<LoginUser.Response> {

        @Override
        public Response<LoginUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<LoginUser.Response> callback) {
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
        public Call<LoginUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}