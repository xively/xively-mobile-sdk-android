package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GetDevices {

        @GET("/api/v1/devices")
        void getDevices(
                @Query("accountId") String accountId,
                @Query("deviceTemplateId") String deviceTemplateId,
                @Query("organizationId") String organizationId,
                @Query("provisioningState") String provisioningState,
                @Query("meta") Boolean meta,
                @Query("results") Boolean results,
                @Query("page") Integer page,
                @Query("pageSize") Integer pageSize,
                @Query("sortBy") String sortBy,
                @Query("sortOrder") String sortOrder,
                Callback<Response> callback);

        class Response {
            public HashMap<String,Object> devices;
            public BlueprintError error;
        }

}
