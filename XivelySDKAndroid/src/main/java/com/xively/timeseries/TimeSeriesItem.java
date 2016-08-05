package com.xively.timeseries;

public class TimeSeriesItem {
    public String time; //Time in ISO8601 (yyyy-MM-ddTHH:mm:ssZ)
    public String category; //Category
    public String numericValue;
    public String stringValue;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(time);
        sb.append("\nCategory: ").append(category);
        sb.append("\nNumeric: ").append(numericValue);
        sb.append("\nString: ").append(stringValue);

        return sb.toString();
    }
}
