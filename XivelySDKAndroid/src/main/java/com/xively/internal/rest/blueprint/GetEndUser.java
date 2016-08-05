package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by milgra on 27/07/16.
 */
public interface GetEndUser {
    @GET("/api/v1/end-users/{id}")
    void getEndUser(@Path("id") String userId, Callback<Response> callback);

    class Response {
        public LinkedTreeMap<String, Object> endUser;
        public BlueprintError error;
    }
}
