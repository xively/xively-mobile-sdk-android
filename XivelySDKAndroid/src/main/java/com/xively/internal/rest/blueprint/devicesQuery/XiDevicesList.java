package com.xively.internal.rest.blueprint.devicesQuery;

import com.xively.internal.rest.blueprint.BlueprintMeta;

import java.util.Arrays;


public class XiDevicesList {
    public XiDeviceData[] results;
    public BlueprintMeta meta;

    @Override
    public String toString() {
        return "XiDevicesList{" +
                "results=" + Arrays.toString(results) +
                ", meta=" + meta +
                '}';
    }
}
