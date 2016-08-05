package com.xively.internal.rest.blueprint;

import com.xively.internal.rest.blueprint.credentialsCreate.Credential;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface CreateCredentials {

    @POST("/api/v1/access/mqtt-credentials")
    void createCredentials(@Body Request body, Callback<Response> callback);

    /**
     * entityId - endUserId
     * entityType - "endUser"
     */
    class Request {
        public String accountId;
        public String entityId;
        public String entityType;
    }

    class Response {
        public Credential mqttCredential;
        public BlueprintError error;
    }
}
