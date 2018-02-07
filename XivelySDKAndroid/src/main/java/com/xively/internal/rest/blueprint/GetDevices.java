package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;


public interface GetDevices {

    @GET("/api/v1/devices")
    Call<Response> getDevices(
            @Header("Authorization") String authHeader,
            @Query("accountId") String accountId,
            @Query("deviceTemplateId") String deviceTemplateId,
            @Query("organizationId") String organizationId,
            @Query("provisioningState") String provisioningState,
            @Query("meta") Boolean meta,
            @Query("results") Boolean results,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );

    class Response {
        public HashMap<String, Object> devices;
        public BlueprintError error;

        @Override
        public String toString() {
            return "Response{" +
                    "devices=" + devices +
                    ", error=" + error +
                    '}';
        }
    }
}
