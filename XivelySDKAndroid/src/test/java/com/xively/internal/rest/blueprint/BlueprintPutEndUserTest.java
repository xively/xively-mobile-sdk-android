package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


public class BlueprintPutEndUserTest extends TestCase {

    @Mock
    private PutEndUser mockPutEndUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testPutDeviceCallsService() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices();


        final String userId = "mock user id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("emailAddress", "anotheremail");
        testWS.putEndUser(userId, version, data, new Callback<PutEndUser.Response>() {
            @Override
            public void onResponse(Call<PutEndUser.Response> call, Response<PutEndUser.Response> response) {

            }

            @Override
            public void onFailure(Call<PutEndUser.Response> call, Throwable t) {

            }
        });

        verify(mockPutEndUser, timeout(500).times(1)).putEndUser(eq(userId), eq(version), eq(data));
    }
}
