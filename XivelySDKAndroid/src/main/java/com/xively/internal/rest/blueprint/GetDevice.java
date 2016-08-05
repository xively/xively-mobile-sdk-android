package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GetDevice {
    @GET("/api/v1/devices/{id}")
    void getDevice( @Path("id") String deviceId, Callback<Response> callback);

    class Response {
        public LinkedTreeMap<String, Object> device;
        public BlueprintError error;
    }

}
