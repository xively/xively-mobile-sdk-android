package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GetOrganization {

    @GET("/api/v1/organizations/{id}")
    Call<Response> getOrganization(
            @Header("Authorization") String authHeader,
            @Path("id") String organizationId
    );

    class Response {
        public LinkedTreeMap<String, Object> organization;
        public BlueprintError error;
    }
}
