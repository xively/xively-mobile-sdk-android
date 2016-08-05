package com.xively.internal.rest.blueprint;

import com.google.gson.internal.LinkedTreeMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by milgra on 27/07/16.
 */
public interface GetOrganization {
    @GET("/api/v1/organizations/{id}")
    void getOrganization(@Path("id") String organizationId, Callback<Response> callback);

    class Response {
        public LinkedTreeMap<String, Object> organization;
        public BlueprintError error;
    }
}
