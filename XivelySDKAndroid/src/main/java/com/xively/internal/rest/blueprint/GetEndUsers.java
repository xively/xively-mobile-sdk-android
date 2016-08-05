package com.xively.internal.rest.blueprint;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GetEndUsers {

    @GET("/api/v1/end-users")
    void getEndUsers(
            @Query("accountId") String accountId,
            @Query("userId") String accessUserId,//xi/idm/
            @Query("meta") Boolean meta,
            @Query("results") Boolean results,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("sortOrder") String sortOrder,
            Callback<Response> callback
            );

    class Response {
        public HashMap<String,Object> endUsers;
        public BlueprintError error;
    }
}
