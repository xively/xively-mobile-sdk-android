package com.xively.internal.timeseries;

import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.timeseries.GetData;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;
import com.xively.timeseries.TimeSeriesItem;
import com.xively.timeseries.XiTimeSeries;
import com.xively.timeseries.XiTimeSeriesCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XiTimeSeriesImpl implements XiTimeSeries {
    private static final String TAG = "XiTimeSeriesImpl";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private final Lock tsDataLock = new ReentrantLock();

    @Override
    public void requestTimeSeriesItemsForChannel(
            String channel,
            Date startDate,
            Date endDate,
            final XiTimeSeriesCallback xiTimeSeriesCallback
    ) {
        requestTimeSeriesItemsForChannel(channel, startDate, endDate, null, xiTimeSeriesCallback);
    }

    @Override
    public void requestTimeSeriesItemsForChannel(
            String channel,
            Date startDate,
            Date endDate,
            String category,
            final XiTimeSeriesCallback xiTimeSeriesCallback
    ) {
        TimeSeriesWebServices ts = DependencyInjector.get().timeSeriesWebServices();
        ts.getData(channel, startDate, endDate, null, null, null, category, null, new Callback<GetData.Response>() {

            @Override
            public void onResponse(Call<GetData.Response> call, Response<GetData.Response> response) {
                GetData.Response getDataResposne = response.body();

                if (getDataResposne.result != null &&
                        getDataResposne.result.length > 0) {
                    ArrayList<TimeSeriesItem> res = new ArrayList<>();
                    res.addAll(Arrays.asList(getDataResposne.result));
                    xiTimeSeriesCallback.onTimeSeriesItemsRetrieved(res);
                } else {
                    xiTimeSeriesCallback.onFinishedWithError(null);
                }
            }

            @Override
            public void onFailure(Call<GetData.Response> call, Throwable t) {
                log.w("TS get data finished");
                xiTimeSeriesCallback.onFinishedWithError(null);
            }
        });
    }

    @Override
    public void cancel() {
        //FIXME: implement
    }

    private ArrayList<TimeSeriesItem> getDataQuery(
            final String channel,
            final Date startDate,
            final Date endDate,
            final String category,
            final String pagingToken
    ) {
        TimeSeriesWebServices ts = DependencyInjector.get().timeSeriesWebServices();

        final ArrayList<TimeSeriesItem> result = new ArrayList<>();
        final Condition resultUpdated = tsDataLock.newCondition();

        try {
            tsDataLock.lock();
            ts.getData(channel, startDate, endDate, null, pagingToken, null, category, null, new Callback<GetData.Response>() {
                @Override
                public void onResponse(Call<GetData.Response> call, Response<GetData.Response> response) {
                    GetData.Response getDataResponse = response.body();

                    if (getDataResponse.result != null &&
                            getDataResponse.result.length > 0) {
                        result.addAll(Arrays.asList(getDataResponse.result));
                    }

                    if (getDataResponse.meta.pagingToken != null &&
                            !getDataResponse.meta.pagingToken.equals("")) {
                        ArrayList<TimeSeriesItem> recursiveResult = null;

                        recursiveResult = getDataQuery(channel, startDate, endDate, category,
                                getDataResponse.meta.pagingToken);

                        if (recursiveResult != null) {
                            result.addAll(recursiveResult);
                        }
                    }

                    resultUpdated.signalAll();
                }

                @Override
                public void onFailure(Call<GetData.Response> call, Throwable t) {
                    log.w("TS get data finished");
                    resultUpdated.signalAll();
                }
            });

            try {
                resultUpdated.await(Config.CONN_HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.w("Timeout while waiting for TimeSeries data.");
            }

        } finally {
            tsDataLock.unlock();
        }

        return (ArrayList<TimeSeriesItem>) result.clone();
    }
}
