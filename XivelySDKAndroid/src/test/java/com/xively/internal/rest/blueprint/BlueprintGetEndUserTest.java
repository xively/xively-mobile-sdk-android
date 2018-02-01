package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetEndUserTest extends TestCase {
    @Mock
    private GetEndUser mockGetEndUser;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testGetEndUserCallsService() throws Exception {
        BlueprintWebServices SUT = new BlueprintWebServices(
                null,
                null,
                null,
                null,
                null,
                mockGetEndUser,
                null,
                null,
                null,
                null
        );

        final String userId = "mock user id";

        when(mockGetEndUser.getEndUser(anyString(), anyString())).thenReturn(new SuccsssStubCall());

        SUT.getEndUser(userId, new Callback<GetEndUser.Response>() {
            @Override
            public void onResponse(Call<GetEndUser.Response> call, Response<GetEndUser.Response> response) {

            }

            @Override
            public void onFailure(Call<GetEndUser.Response> call, Throwable t) {

            }
        });

        verify(mockGetEndUser, times(1)).getEndUser(anyString(), eq(userId));
    }

    private class SuccsssStubCall implements Call<GetEndUser.Response> {
        @Override
        public Response<GetEndUser.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetEndUser.Response> callback) {

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
        public Call<GetEndUser.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
