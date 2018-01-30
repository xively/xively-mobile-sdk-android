package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GetEndUsers {

    @GET("/api/v1/end-users")
    Call<Response> getEndUsers(
            @Query("accountId") String accountId,
            @Query("userId") String accessUserId,//xi/idm/
            @Query("meta") Boolean meta,
            @Query("results") Boolean results,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("sortOrder") String sortOrder
    );

    class Response {
        public HashMap<String, Object> endUsers;
        public BlueprintError error;
    }
}
