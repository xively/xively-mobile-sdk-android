package com.xively.internal.rest.provision;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface StartAssociationWithCode {

    @POST("/api/v1/association/start-association-with-code")
    Call<Response> startAssociationWithCode(
            @Header("Authorization") String credentials,
            @Body Request body
    );

    class Request {
        public String associationCode;
        public String endUserId;
    }

    class Response {

    }
}
