package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Callback;
import retrofit2.Retrofit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetEndUserTest extends TestCase {
    @Mock
    Retrofit mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetEndUserCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();

        Callback<GetEndUser.Response> mockCallback = mock(Callback.class);
        GetEndUser mockGetEndUser = mock(GetEndUser.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetEndUser);

        final String userId = "mock user id";
        testWS.getEndUser(userId, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockGetEndUser, times(1)).getEndUser(eq(userId));
    }
}
