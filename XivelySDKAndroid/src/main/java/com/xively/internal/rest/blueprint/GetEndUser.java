package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;


public interface GetEndUser {

    @GET("/api/v1/end-users/{id}")
    Call<Response> getEndUser(
            @Header("Authorization") String authHeader,
            @Path("id") String userId
    );

    class Response {
        public LinkedTreeMap<String, Object> endUser;
        public BlueprintError error;
    }
}
