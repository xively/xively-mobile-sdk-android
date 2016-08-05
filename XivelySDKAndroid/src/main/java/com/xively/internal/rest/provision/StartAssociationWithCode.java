package com.xively.internal.rest.provision;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface StartAssociationWithCode {

    @POST("/api/v1/association/start-association-with-code")
    void startAssociationWithCode(@Body Request body, Callback<Response> callback);

    class Request {
        public String associationCode;
        public String endUserId;
    }

    class Response {
    }
}
