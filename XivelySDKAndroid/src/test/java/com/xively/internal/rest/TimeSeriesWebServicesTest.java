package com.xively.internal.rest;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.timeseries.GetData;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeSeriesWebServicesTest extends TestCase {

    @Mock
    private RestAdapter mockRestAdapter;

    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US); //ISO 8601

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testTimeSeriesGetData() throws Exception {
        TimeSeriesWebServices testWS = new TimeSeriesWebServices(mockRestAdapter);

        Callback<GetData.Response> mockCallback = mock(Callback.class);
        GetData mockGetData = mock(GetData.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetData);

        String mockTopic = "mock topic value";
        Date mockStartDate = new Date(System.currentTimeMillis() - 24 * 60 * 60000);
        Date mockEndDate = new Date(System.currentTimeMillis());
        String expectedStartDate = isoFormat.format(mockStartDate);
        String expectedEndDate = isoFormat.format(mockEndDate);

        testWS.getData(mockTopic, mockStartDate, mockEndDate, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<GetData>>anyObject());

        verify(mockGetData, timeout(1000).times(1)).getData( eq(mockTopic),
                eq(expectedStartDate), eq(expectedEndDate), anyInt(), anyString(), anyBoolean(),
                anyString(), anyInt(), eq(mockCallback));
    }

    public void testTimeSeriesGetDataAllParams() throws Exception {
        TimeSeriesWebServices testWS = new TimeSeriesWebServices(mockRestAdapter);

        Callback<GetData.Response> mockCallback = mock(Callback.class);
        GetData mockGetData = mock(GetData.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetData);

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

        testWS.getData(mockTopic, mockStartDate, mockEndDate, mockPageSize, mockToken,
                mockOmitNull, mockCategory, mockGroupType, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<GetData>>anyObject());

        verify(mockGetData, timeout(1000).times(1)).getData(eq(mockTopic),
                eq(expectedStartDate), eq(expectedEndDate), eq(mockPageSize), eq(mockToken),
                eq(mockOmitNull), eq(mockCategory), eq(mockGroupType),
                eq(mockCallback));
    }

}