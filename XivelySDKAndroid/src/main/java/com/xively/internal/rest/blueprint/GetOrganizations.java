package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;


public interface GetOrganizations {

    @GET("/api/v1/organizations")
    Call<Response> getOrganizations(
            @Header("Authorization") String authHeader,
            @Query("accountId") String accountId,
            @Query("parentId") String parentId,
            @Query("deviceTemplateId") String deviceTemplateId,
            @Query("organizationTemplateId") String organizationTemplateId,
            @Query("name") String name,
            @Query("meta") Boolean meta,
            @Query("results") Boolean results,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );

    class Response {
        public HashMap<String, Object> organizations;
        public BlueprintError error;
    }
}
