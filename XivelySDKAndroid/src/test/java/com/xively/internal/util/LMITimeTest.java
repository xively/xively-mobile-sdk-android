package com.xively.internal.util;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import static org.mockito.Mockito.*;

public class LMITimeTest extends TestCase {

    @Mock
    Calendar mockCalendar;

    LMITime testObject;
    private final int expectedYear  = 2015;
    private final int expectedMonth = 3;
    private final int expectedDay = 5;
    private final int expectedHour = 22;
    private final int expectedMinute = 59;
    private final int expectedSecond = 0;

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        doReturn(expectedYear).when(mockCalendar).get(Calendar.YEAR);
        doReturn(expectedMonth - 1).when(mockCalendar).get(Calendar.MONTH);
        doReturn(expectedDay).when(mockCalendar).get(Calendar.DAY_OF_MONTH);
        doReturn(expectedHour).when(mockCalendar).get(Calendar.HOUR_OF_DAY);
        doReturn(expectedMinute).when(mockCalendar).get(Calendar.MINUTE);
        doReturn(expectedSecond).when(mockCalendar).get(Calendar.SECOND);

        testObject = new LMITime(mockCalendar);
    }

    public void testGetYear() throws Exception {
        int res = testObject.getYear();
        verify(mockCalendar, times(1)).get(Calendar.YEAR);
        assertEquals(res, expectedYear);
    }

    public void testGetMonth() throws Exception {
        int res = testObject.getMonth();
        verify(mockCalendar, times(1)).get(Calendar.MONTH);
        assertEquals(res, expectedMonth);
    }

    public void testGetDay() throws Exception {
        int res = testObject.getDay();
        verify(mockCalendar, times(1)).get(Calendar.DAY_OF_MONTH);
        assertEquals(res, expectedDay);
    }

    public void testGetHour() throws Exception {
        int res = testObject.getHour();
        verify(mockCalendar, times(1)).get(Calendar.HOUR_OF_DAY);
        assertEquals(res, expectedHour);
    }

    public void testGetMinute() throws Exception {
        int res = testObject.getMinute();
        verify(mockCalendar, times(1)).get(Calendar.MINUTE);
        assertEquals(res, expectedMinute);
    }

    public void testGetSecond() throws Exception {
        int res = testObject.getSecond();
        verify(mockCalendar, times(1)).get(Calendar.SECOND);
        assertEquals(res, expectedSecond);
    }

}