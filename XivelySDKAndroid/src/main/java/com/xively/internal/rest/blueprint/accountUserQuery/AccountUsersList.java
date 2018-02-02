package com.xively.internal.rest.blueprint.accountUserQuery;

import com.xively.internal.rest.blueprint.BlueprintMeta;

import java.util.Arrays;


public class AccountUsersList {
    public AccountUser[] results;
    public BlueprintMeta meta;

    @Override
    public String toString() {
        return "AccountUsersList{" +
                "results=" + Arrays.toString(results) +
                ", meta=" + meta +
                '}';
    }
}
