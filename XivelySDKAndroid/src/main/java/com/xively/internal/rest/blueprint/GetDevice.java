package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDevice {
    @GET("/api/v1/devices/{id}")
    Call<Response> getDevice(@Path("id") String deviceId);

    class Response {
        public LinkedTreeMap<String, Object> device;
        public BlueprintError error;
    }

}
