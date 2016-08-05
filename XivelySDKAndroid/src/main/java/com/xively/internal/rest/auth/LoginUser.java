package com.xively.internal.rest.auth;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface LoginUser {

    @POST("/api/v1/auth/login-user")
    void loginUser(@Body Request body, Callback<Response> callback);

    public class Request {
        public String emailAddress;
        public String password;
        public String accountId;
    }

    public class Response {
        public String jwt;
    }
}
