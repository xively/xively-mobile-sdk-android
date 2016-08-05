package com.xively.internal.rest;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.auth.LoginUser;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthWebServicesTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testLoginUser() throws Exception {
        AuthWebServices testAuthWS = new AuthWebServices(mockRestAdapter);

        Callback<LoginUser.Response> mockCallback = mock(Callback.class);
        LoginUser mockLoginUserWS = mock(LoginUser.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockLoginUserWS);

        String username = "mock username";
        String password = "mock password 123@";
        String accountId = "mock account Id";

        ArgumentCaptor<LoginUser.Request> loginUserRequestCaptor
                = ArgumentCaptor.forClass(LoginUser.Request.class);
        testAuthWS.loginUser(username, password, accountId, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<Object>>anyObject());

        verify(mockLoginUserWS, timeout(100).times(1))
                .loginUser(loginUserRequestCaptor.capture(), eq(mockCallback));
        LoginUser.Request loginRequest = loginUserRequestCaptor.getValue();

        assertNotNull(loginRequest);
        assertEquals(username, loginRequest.emailAddress);
        assertEquals(password, loginRequest.password);
        assertEquals(accountId, loginRequest.accountId);
    }

}