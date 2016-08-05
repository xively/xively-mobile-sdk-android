package com.xively.timeseries;

import java.util.ArrayList;

public interface XiTimeSeriesCallback {

    enum XiTimeSeriesError {};

    void onTimeSeriesItemsRetrieved(ArrayList<TimeSeriesItem> items);
    void onFinishedWithError(XiTimeSeriesError error);
    void onCancelled();

}
