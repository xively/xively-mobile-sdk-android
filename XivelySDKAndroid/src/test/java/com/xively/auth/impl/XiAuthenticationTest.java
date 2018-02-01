package com.xively.auth.impl;

import com.xively.XiSdkConfig;
import com.xively.XiSession;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.auth.XiAuthenticationImpl;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.auth.LoginUser;
import com.xively.internal.rest.blueprint.BlueprintWebServices;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.SocketTimeoutException;

import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XiAuthenticationTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private AuthWebServices mockAuthWebServices;
    @Mock
    private ProvisionWebServices mockProvisionWebServices;
    @Mock
    private BlueprintWebServices mockBlueprintWebServices;
    @Mock
    private TimeSeriesWebServices mockTSWebServices;
    @Mock
    private XiAuthenticationCallback mockAuthCallback;

    @Captor
    private ArgumentCaptor<XiAuthenticationCallback.XiAuthenticationError> errorCaptor;
    @Captor
    private ArgumentCaptor<Callback<LoginUser.Response>> captorLoginResponse;
    @Captor
    private ArgumentCaptor<Callback<XivelyAccount>> captorXivelyAccountResponse;

    private final String mockEmail = "mockEmail@address.net";
    private final String mockPassword = "mock PaSsWord@#";
    private final String mockAccountId = "mock account Id";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);

        MockitoAnnotations.initMocks(this);

        when(mockDependencyInjector.authWebServices()).thenReturn(mockAuthWebServices);
        when(mockDependencyInjector.provisionWebServices()).thenReturn(mockProvisionWebServices);
        when(mockDependencyInjector.blueprintWebServices()).thenReturn(mockBlueprintWebServices);
        when(mockDependencyInjector.timeSeriesWebServices()).thenReturn(mockTSWebServices);

        DependencyInjector.setInstance(mockDependencyInjector);

        errorCaptor = ArgumentCaptor.forClass(XiAuthenticationCallback.XiAuthenticationError.class);
    }

    public void testRequestAuthSuccessOnValidLoginWSResponse() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";

        loginCallback.onResponse(null, Response.success(response));

        //got jwt, credentials provisioning should start
        verify(mockBlueprintWebServices).queryXivelyAccount(
                eq(response.jwt),
                captorXivelyAccountResponse.capture()
        );

        Callback<XivelyAccount> xivelyAccountCallback = captorXivelyAccountResponse.getValue();
        assertNotNull(xivelyAccountCallback);

        xivelyAccountCallback.onResponse(null, Response.success(new XivelyAccount("client Id", "user", "password")));

        verify(mockProvisionWebServices).setBearerAuthorizationHeader(eq("mock jwt"));
        verify(mockAuthCallback).sessionCreated(Matchers.<XiSession>anyObject());
    }

    public void testRequestAuthFailureCallbackOnNullLoginWSResponse() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(eq(mockEmail), eq(mockPassword), eq(mockAccountId),
                captorLoginResponse.capture());

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        loginCallback.onResponse(null, Response.success(new LoginUser.Response()));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
    }

    public void testRequestAuthFailureCallbackOnInvalidLoginWSResponse() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = null;

        loginCallback.onResponse(null, Response.success(response));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
    }

    public void testRequestAuthFailureCallbackOnEmptyLoginWSResponse() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "";

        loginCallback.onResponse(null, Response.success(response));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
    }

    public void testRequestAuthHandlesLoginWSFailure() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        assertNotNull(loginCallback);

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";

        loginCallback.onFailure(null, new Throwable("x", new NullPointerException()));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.UNEXPECTED_ERROR);
    }

    public void testRequestAuthFailureCallbackOnCancel() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();
        assertNotNull(loginCallback);

        SUT.cancel();

        LoginUser.Response response = new LoginUser.Response();
        response.jwt = "mock jwt";

        loginCallback.onResponse(null, Response.success(response));

        verify(mockProvisionWebServices, atLeastOnce()).setBearerAuthorizationHeader(anyString());
        verify(mockTSWebServices, atLeastOnce()).setBearerAuthorizationHeader(anyString());
        verify(mockAuthCallback, never()).sessionCreated(Matchers.<XiSession>anyObject());
        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.CANCELED);
    }

    public void testRequestAuthFailureOnNetworkError() {
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();

        loginCallback.onFailure(null, new Throwable("mock url", new SocketTimeoutException()));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.NETWORK_ERROR);
    }

    public void testRequestAuthFailureOnInvalidCredentials() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();

        LoginUser.Response loginUserResponse = new LoginUser.Response();
        loginUserResponse.jwt = null;
        loginUserResponse.error = "Unathorized";

        loginCallback.onResponse(null, Response.success(loginUserResponse));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.INVALID_CREDENTIALS);
    }

    public void testRequestAuthFailureOnServer() {
        XiAuthenticationImpl SUT = new XiAuthenticationImpl();

        SUT.requestAuth(mockEmail, mockPassword, mockAccountId, mockAuthCallback);

        verify(mockAuthWebServices).loginUser(
                eq(mockEmail),
                eq(mockPassword),
                eq(mockAccountId),
                captorLoginResponse.capture()
        );

        Callback<LoginUser.Response> loginCallback = captorLoginResponse.getValue();

        LoginUser.Response loginUserResponse = new LoginUser.Response();
        loginUserResponse.jwt = null;
        loginUserResponse.error = "InternalServerError";

        loginCallback.onResponse(null, Response.success(loginUserResponse));

        verify(mockAuthCallback).authenticationFailed(errorCaptor.capture());

        assertNotNull(errorCaptor.getValue());
        assertEquals(errorCaptor.getValue(), XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
    }
}
