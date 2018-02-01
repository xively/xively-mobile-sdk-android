package com.xively.internal.rest.blueprint;

import com.xively.internal.rest.blueprint.credentialsCreate.Credential;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CreateCredentials {

    @POST("/api/v1/access/mqtt-credentials")
    Call<Response> createCredentials(
            @Header("Authorization") String authHeader,
            @Body Request body
    );

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
