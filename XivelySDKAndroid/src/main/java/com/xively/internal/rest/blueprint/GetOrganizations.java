package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by milgra on 26/07/16.
 */
public interface GetOrganizations {

    @GET("/api/v1/organizations")
    void getOrganizations(
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
            @Query("sortOrder") String sortOrder,
            Callback<Response> callback);

    class Response {
        public HashMap<String,Object> organizations;
        public BlueprintError error;
    }
}
