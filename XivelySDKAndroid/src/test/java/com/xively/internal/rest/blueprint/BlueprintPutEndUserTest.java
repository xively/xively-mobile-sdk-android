package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BlueprintPutEndUserTest extends TestCase {

    @Mock
    private PutEndUser mockPutEndUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testPutDeviceCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                null,
                mockPutEndUser,
                null,
                null,
                null
        );

        final String userId = "mock user id";
        final String version = "ef";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("emailAddress", "anotheremail");

        when(mockPutEndUser.putEndUser(anyString(), anyString(), anyString(), Matchers.<HashMap<String, Object>>any())).thenReturn(new SuccessStubCall());

        SUT.putEndUser(userId, version, data, new Callback<PutEndUser.Response>() {
            @Override
            public void onResponse(Call<PutEndUser.Response> call, Response<PutEndUser.Response> response) {

            }

            @Override
            public void onFailure(Call<PutEndUser.Response> call, Throwable t) {

            }
        });

        verify(mockPutEndUser, timeout(500).times(1)).putEndUser(anyString(), eq(userId), eq(version), eq(data));
    }

    private class SuccessStubCall implements Call<PutEndUser.Response> {

        @Override
        public Response<PutEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<PutEndUser.Response> callback) {

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
        public Call<PutEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
