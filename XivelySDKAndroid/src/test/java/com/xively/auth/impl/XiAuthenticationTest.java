package com.xively.auth.impl;

import com.xively.XiSdkConfig;
import com.xively.XiSession;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.auth.XiAuthenticationImpl;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.access.AccessWebServices;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.auth.LoginUser;
import com.xively.internal.rest.blueprint.BlueprintWebServices;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XiAuthenticationTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private AuthWebServices mockAuthWebServices;
    @Mock
    private AccessWebServices mockAccessWebServices;
    @Mock
    private ProvisionWebServices mockProvisionWebServices;
    @Mock
    private BlueprintWebServices mockBlueprintWebServices;
    @Mock
    private TimeSeriesWebServices mockTSWebServices;
    @Mock
    private XiAuthenticationCallback mockAuthCallback;

    private ArgumentCaptor<XiAuthenticationCallback.XiAuthenticationError> errorCaptor;

    private final String mockEmail = "mockEmail@address.net";
    private final String mockPassword = "mock PaSsWord@#";
    private final String mockAccountId = "mock account Id";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);

        MockitoAnnotations.initMocks(this);

        when(mockDependencyInjector.authWebServices()).thenReturn(mockAuthWebServices);
        when(mockDependencyInjector.accessWebServices()).thenReturn(mockAccessWebServices);
        when(mockDependencyInjector.provisionWebServices()).thenReturn(mockProvisionWebServices);
        when(mockDependencyInjector.blueprintWebServices()).thenReturn(mockBlueprintWebServices);
        when(mockDependencyInjector.timeSeriesWebServices()).thenReturn(mockTSWebServices);

        DependencyInjector.setInstance(mockDependencyInjector);

        errorCaptor = ArgumentCaptor.forClass(XiAuthenticationCallback.XiAuthenticationError.class);
    }

    public void testRequestAuthSuccessOnValidLoginWSResponse(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Callback> queryXAccountCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);

        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";
        loginCallback.success(response, null);

        //got jwt, credentials provisioning should start
        verify(mockBlueprintWebServices).queryXivelyAccount(eq(response.jwt),
                queryXAccountCallbackCaptor.capture());
        Callback<XivelyAccount> xivelyAccountCallback = queryXAccountCallbackCaptor.getValue();
        assertNotNull(xivelyAccountCallback);

        xivelyAccountCallback.success(new XivelyAccount("client Id", "user", "password"), null);

        verify(mockProvisionWebServices).setBearerAuthorizationHeader(eq("mock jwt"));
        verify(mockAuthCallback).sessionCreated(Matchers.<XiSession>anyObject());
    }

    public void testRequestAuthFailureCallbackOnNullLoginWSResponse(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();

        loginCallback.success(null, null);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR));
    }

    public void testRequestAuthFailureCallbackOnInvalidLoginWSResponse(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = null;
        loginCallback.success(response, null);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR));
    }

    public void testRequestAuthFailureCallbackOnEmptyLoginWSResponse(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "";
        loginCallback.success(response, null);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR));
    }

    public void testRequestAuthHandlesLoginWSFailure(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";
        loginCallback.failure(RetrofitError.unexpectedError("x", new NullPointerException()));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.UNEXPECTED_ERROR));
    }

    public void testRequestAuthFailureCallbackOnCancel(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();
        assertNotNull(loginCallback);

        testAuth.cancel();

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";
        loginCallback.success(response, null);

        verify(mockProvisionWebServices, atLeastOnce()).setBearerAuthorizationHeader(anyString());
        verify(mockTSWebServices, atLeastOnce()).setBearerAuthorizationHeader(anyString());
        verify(mockAuthCallback, never()).sessionCreated(Matchers.<XiSession>anyObject());
        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.CANCELED));
    }

    public void testRequestAuthFailureOnNetworkError(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();

        RetrofitError mockError =
                RetrofitError.networkError("mock url", new SocketTimeoutException());
        loginCallback.failure(mockError);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.NETWORK_ERROR));
    }

    //TODO: invalid credentials

    public void testRequestAuthFailureOnInvalidCredentials(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();

        retrofit.client.Response mockResponse = new Response("mock url", 401, "something wrong",
                new ArrayList<retrofit.client.Header>(), null);
        RetrofitError mockError =
                RetrofitError.httpError("mock url", mockResponse, null, null);

        loginCallback.failure(mockError);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.INVALID_CREDENTIALS));
    }

    public void testRequestAuthFailureOnServer(){
        ArgumentCaptor<Callback> loginResponseCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                loginResponseCallbackCaptor.capture());
        Callback<LoginUser.Response> loginCallback = loginResponseCallbackCaptor.getValue();

        retrofit.client.Response mockResponse = new Response("mock url", 503, "something bad",
                new ArrayList<retrofit.client.Header>(), null);
        RetrofitError mockError =
                RetrofitError.httpError("mock url", mockResponse, null, null);

        loginCallback.failure(mockError);

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertTrue(errorCaptor.getValue()
                .equals(XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR));
    }

}
