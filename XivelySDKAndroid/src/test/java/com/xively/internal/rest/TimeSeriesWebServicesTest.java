package com.xively.internal.rest;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.timeseries.GetData;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeSeriesWebServicesTest extends TestCase {

    private Callback<GetData.Response> callback;

    @Mock
    private GetData mockGetData;

    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US); //ISO 8601

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testTimeSeriesGetData() throws Exception {
        TimeSeriesWebServices SUT = new TimeSeriesWebServices(mockGetData);

        String mockTopic = "mock topic value";
        Date mockStartDate = new Date(System.currentTimeMillis() - 24 * 60 * 60000);
        Date mockEndDate = new Date(System.currentTimeMillis());
        String expectedStartDate = isoFormat.format(mockStartDate);
        String expectedEndDate = isoFormat.format(mockEndDate);

        when(mockGetData.getData(anyString(), anyString(), anyString(), anyInt(), anyString(), anyBoolean(), anyString(), anyInt())).thenReturn(new SuccessStubCall());

        callback = new Callback<GetData.Response>() {
            @Override
            public void onResponse(Call<GetData.Response> call, Response<GetData.Response> response) {

            }

            @Override
            public void onFailure(Call<GetData.Response> call, Throwable t) {

            }
        };

        SUT.getData(mockTopic, mockStartDate, mockEndDate, callback);

        verify(mockGetData, times(1)).getData(eq(mockTopic),
                eq(expectedStartDate), eq(expectedEndDate), anyInt(), anyString(), anyBoolean(),
                anyString(), anyInt());
    }

    public void testTimeSeriesGetDataAllParams() throws Exception {
        TimeSeriesWebServices SUT = new TimeSeriesWebServices(mockGetData);

        String mockTopic = "mock topic value";
        Date mockStartDate = new Date(System.currentTimeMillis() - 24 * 60 * 60000);
        Date mockEndDate = new Date(System.currentTimeMillis());
        String expectedStartDate = isoFormat.format(mockStartDate);
        String expectedEndDate = isoFormat.format(mockEndDate);
        Integer mockPageSize = 123;
        String mockToken = "mock token";
        Boolean mockOmitNull = Boolean.TRUE;
        String mockCategory = "mock category";
        Integer mockGroupType = 123456;

        when(mockGetData.getData(anyString(), anyString(), anyString(), anyInt(), anyString(), anyBoolean(), anyString(), anyInt())).thenReturn(new SuccessStubCall());

        callback = new Callback<GetData.Response>() {
            @Override
            public void onResponse(Call<GetData.Response> call, Response<GetData.Response> response) {

            }

            @Override
            public void onFailure(Call<GetData.Response> call, Throwable t) {

            }
        };

        SUT.getData(mockTopic, mockStartDate, mockEndDate, mockPageSize, mockToken,
                mockOmitNull, mockCategory, mockGroupType, callback);

        verify(mockGetData, times(1)).getData(eq(mockTopic),
                eq(expectedStartDate), eq(expectedEndDate), eq(mockPageSize), eq(mockToken),
                eq(mockOmitNull), eq(mockCategory), eq(mockGroupType));
    }

    private class SuccessStubCall implements Call<GetData.Response> {

        @Override
        public Response<GetData.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetData.Response> callback) {
            GetData.Response response = new GetData.Response();
            Response<GetData.Response> retrofitResponse = Response.success(response);
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
        public Call<GetData.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }

    private class FailureStubCall implements Call<GetData.Response> {

        @Override
        public Response<GetData.Response> execute() throws IOException {
            return null;
        }

        @Override
        public void enqueue(Callback<GetData.Response> callback) {
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
        public Call<GetData.Response> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}