package com.xively.internal.rest.blueprint.endUserQuery;

import com.xively.internal.rest.blueprint.BlueprintMeta;

import java.util.Arrays;


public class EndUsersListResponse {
    public EndUser[] results;
    public BlueprintMeta meta;

    @Override
    public String toString() {
        return "EndUsersListResponse{" +
                "results=" + Arrays.toString(results) +
                ", meta=" + meta +
                '}';
    }
}
