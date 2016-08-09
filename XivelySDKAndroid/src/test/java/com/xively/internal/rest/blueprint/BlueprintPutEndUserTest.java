package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiDeviceUpdateInfo;
import com.xively.messaging.XiEndUserUpdateInfo;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by milgra on 29/07/16.
 */
public class BlueprintPutEndUserTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testPutDeviceCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<PutEndUser.Response> mockCallback = mock(Callback.class);
        PutEndUser mockPutEndUser = mock(PutEndUser.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockPutEndUser);

        final String userId = "mock user id";
        final String version = "ef";

        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("emailAddress","anotheremail");
        testWS.putEndUser( userId, version, data, mockCallback );

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockPutEndUser, timeout(500).times(1)).putEndUser(eq(userId),eq(version),eq(data), eq(mockCallback));

    }

}
