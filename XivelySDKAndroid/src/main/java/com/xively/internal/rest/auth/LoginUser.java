package com.xively.internal.rest.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface LoginUser {

    @POST("/api/v1/auth/login-user")
    Call<Response> loginUser(@Body Request body);

    class Request {
        public String emailAddress;
        public String password;
        public String accountId;

        @Override
        public String toString() {
            return "LoginUser.Request{" +
                    "emailAddress='" + emailAddress + '\'' +
                    ", password='" + password + '\'' +
                    ", accountId='" + accountId + '\'' +
                    '}';
        }
    }

    class Response {
        public String jwt;
        public String error;

        @Override
        public String toString() {
            return "LoginUser.Response{" +
                    "jwt='" + jwt + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}
