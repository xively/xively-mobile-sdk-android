package com.xively.timeseries;


public class TimeSeriesItem {
    public String time; //Time in ISO8601 (yyyy-MM-ddTHH:mm:ssZ)
    public String category; //Category
    public String numericValue;
    public String stringValue;

    @Override
    public String toString() {
        return "TimeSeriesItem{" +
                "time='" + time + '\'' +
                ", category='" + category + '\'' +
                ", numericValue='" + numericValue + '\'' +
                ", stringValue='" + stringValue + '\'' +
                '}';
    }
}
