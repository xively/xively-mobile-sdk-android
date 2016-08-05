package com.xively.auth.impl;

import com.xively.XiSdkConfig;
import com.xively.XiSession;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.auth.XiOAuthCallback;
import com.xively.internal.DependencyInjector;
import com.xively.internal.auth.XiAuthenticationImpl;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.access.AccessWebServices;
import com.xively.internal.rest.access.GetOAuthUrl;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.blueprint.BlueprintWebServices;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XiOAuthenticationTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private AuthWebServices mockAuthWebServices;
    @Mock
    private AccessWebServices mockAccessWebServices;
    @Mock
    private ProvisionWebServices mockProvisionWebServices;
    @Mock
    private TimeSeriesWebServices mockTSWebServices;
    @Mock
    private BlueprintWebServices mockBlueprintWebServices;
    @Mock
    private XiOAuthCallback mockOAuthCallback;
    @Mock
    private XiAuthenticationCallback mockAuthCallback;

    private final String mockAccountId = "mock account Id";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);

        MockitoAnnotations.initMocks(this);

        when(mockDependencyInjector.authWebServices()).thenReturn(mockAuthWebServices);
        when(mockDependencyInjector.accessWebServices()).thenReturn(mockAccessWebServices);
        when(mockDependencyInjector.timeSeriesWebServices()).thenReturn(mockTSWebServices);
        when(mockDependencyInjector.blueprintWebServices()).thenReturn(mockBlueprintWebServices);
        when(mockDependencyInjector.provisionWebServices()).thenReturn(mockProvisionWebServices);

        DependencyInjector.setInstance(mockDependencyInjector);
    }

    public void testRequestOAuthReturnsUri(){
        ArgumentCaptor<Callback> oAuthCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestOAuth(mockAccountId, mockOAuthCallback);

        verify(mockAccessWebServices).getOAuthUrl(eq(mockAccountId), oAuthCallbackCaptor.capture());
        Callback<GetOAuthUrl.Response> oAuthCallback = oAuthCallbackCaptor.getValue();
        assertNotNull(oAuthCallback);

        GetOAuthUrl.Response response = new GetOAuthUrl.Response();
        response.location = "mock url response";
        oAuthCallback.success(response, null);

        verify(mockOAuthCallback).oAuthUriReceived(eq("mock url response"));
    }

    public void testRequestOAuthFailedCallbackOnNullWSResponse(){
        ArgumentCaptor<Callback> oAuthCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestOAuth(mockAccountId, mockOAuthCallback);

        verify(mockAccessWebServices).getOAuthUrl(eq(mockAccountId), oAuthCallbackCaptor.capture());
        Callback<GetOAuthUrl.Response> oAuthCallback = oAuthCallbackCaptor.getValue();
        assertNotNull(oAuthCallback);

        oAuthCallback.success(null, null);

        verify(mockOAuthCallback).authenticationFailed();
    }

    public void testRequestOAuthFailedCallbackOnInvalidWSResponse(){
        ArgumentCaptor<Callback> oAuthCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestOAuth(mockAccountId, mockOAuthCallback);

        verify(mockAccessWebServices).getOAuthUrl(eq(mockAccountId), oAuthCallbackCaptor.capture());
        Callback<GetOAuthUrl.Response> oAuthCallback = oAuthCallbackCaptor.getValue();
        assertNotNull(oAuthCallback);

        GetOAuthUrl.Response response = new GetOAuthUrl.Response();
        response.location = null;
        oAuthCallback.success(response, null);

        verify(mockOAuthCallback).authenticationFailed();
    }

    public void testRequestOAuthFailedCallbackOnEmptyWSResponse(){
        ArgumentCaptor<Callback> oAuthCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestOAuth(mockAccountId, mockOAuthCallback);

        verify(mockAccessWebServices).getOAuthUrl(eq(mockAccountId), oAuthCallbackCaptor.capture());
        Callback<GetOAuthUrl.Response> oAuthCallback = oAuthCallbackCaptor.getValue();
        assertNotNull(oAuthCallback);

        GetOAuthUrl.Response response = new GetOAuthUrl.Response();
        response.location = "";
        oAuthCallback.success(response, null);

        verify(mockOAuthCallback).authenticationFailed();
    }

    public void testSetOauthToken(){
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.setOauthToken("mock token", mockAuthCallback);

        verify(mockAuthCallback).sessionCreated(Matchers.<XiSession>anyObject());
    }

    public void testRequestOAuthFailureCallbackOnCancel(){
        ArgumentCaptor<Callback> oAuthCallbackCaptor =
                ArgumentCaptor.forClass(Callback.class);
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();

        testAuth.requestOAuth(mockAccountId, mockOAuthCallback);

        verify(mockAccessWebServices).getOAuthUrl(eq(mockAccountId), oAuthCallbackCaptor.capture());
        Callback<GetOAuthUrl.Response> oAuthCallback = oAuthCallbackCaptor.getValue();
        assertNotNull(oAuthCallback);

        testAuth.cancel();

        GetOAuthUrl.Response response = new GetOAuthUrl.Response();
        response.location = "mock url response";
        oAuthCallback.success(response, null);

        verify(mockOAuthCallback, never()).oAuthUriReceived(anyString());
        verify(mockOAuthCallback).authenticationFailed();
    }

    public void testSetOAuthTokenFailureCallbackOnCancel(){
        XiAuthenticationImpl testAuth = new XiAuthenticationImpl();
        testAuth.cancel();

        testAuth.setOauthToken("mock token", mockAuthCallback);
        verify(mockAuthCallback, never()).sessionCreated(Matchers.<XiSession>anyObject());
        verify(mockAuthCallback).authenticationFailed(
                Matchers.<XiAuthenticationCallback.XiAuthenticationError>anyObject());
    }

}
