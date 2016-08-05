package com.xively.internal.rest.blueprint;


import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface PutDevice
{
    @PUT("/api/v1/devices/{id}")
    void putDevice(@Path("id") String deviceId, @Header("etag") String version, @Body HashMap<String,Object> body, Callback<Response> callback );

    class Response {
        public LinkedTreeMap<String, Object> device;
        public BlueprintError error;
    }
}
