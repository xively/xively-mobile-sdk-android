package com.xively.internal.rest.timeseries;

public class TimeSeriesListMetaDataDTO {
    public Integer timeSpent; //Request time in milliseconds,
    public String start; //Time of the first data in result list
    public String end; //Time of the last data in result list
    public Integer count; //Number of elements in result list
    public String pagingToken; //Paging token

}
