package com.xively.internal.rest.blueprint;


import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface PutDevice {

    @PUT("/api/v1/devices/{id}")
    Call<Response> putDevice(
            @Header("Authorization") String authHeader,
            @Path("id") String deviceId,
            @Header("etag") String version,
            @Body HashMap<String, Object> body
    );

    class Response {
        public LinkedTreeMap<String, Object> device;
        public BlueprintError error;
    }
}
