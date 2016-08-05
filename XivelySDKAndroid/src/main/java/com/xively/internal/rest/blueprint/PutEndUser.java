package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.messaging.XiEndUserUpdateInfo;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by milgra on 29/07/16.
 */
public interface PutEndUser {
    @PUT("/api/v1/end-users/{id}")
    void putEndUser(@Path("id") String userId, @Header("etag") String version, @Body HashMap<String,Object> body, Callback<Response> callback );

    class Response {
        public LinkedTreeMap<String, Object> endUser;
        public BlueprintError error;
    }
}
