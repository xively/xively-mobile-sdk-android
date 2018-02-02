package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface PutEndUser {

    @PUT("/api/v1/end-users/{id}")
    Call<Response> putEndUser(
            @Header("Authorization") String authHeader,
            @Path("id") String userId,
            @Header("etag") String version,
            @Body HashMap<String, Object> body
    );

    class Response {
        public LinkedTreeMap<String, Object> endUser;
        public BlueprintError error;
    }
}
