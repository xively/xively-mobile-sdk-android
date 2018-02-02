package com.xively.internal.rest.blueprint.organizationsQuery;

import com.xively.internal.rest.blueprint.BlueprintMeta;

import java.util.Arrays;


public class XiOrganizationsList {
    public XiOrganizationData[] results;
    public BlueprintMeta meta;

    @Override
    public String toString() {
        return "XiOrganizationsList{" +
                "results=" + Arrays.toString(results) +
                ", meta=" + meta +
                '}';
    }
}
