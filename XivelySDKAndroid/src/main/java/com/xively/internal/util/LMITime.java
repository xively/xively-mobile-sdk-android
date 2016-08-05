package com.xively.internal.util;

import java.util.Calendar;

public class LMITime {

    private final Calendar mCalendar;

    public LMITime() {
        mCalendar = Calendar.getInstance();
    }

    public LMITime(Calendar calendar) {
        mCalendar = calendar;
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public int getSecond() {
        return mCalendar.get(Calendar.SECOND);
    }

    public String getFormatted(final String format) {
        return String.format(format, mCalendar);
    }
}
