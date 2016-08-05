package com.xively.internal.rest.blueprint;

import com.xively.internal.rest.blueprint.accountUserQuery.AccountUsersList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GetAccountUser {

    @GET("/api/v1/account-users")
    void getAccountUser(
            @Query("accountId") String accountId,
            @Query("userId") String accessUserId,
            @Query("meta") Boolean meta,
            @Query("results") Boolean results,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("sortOrder") String sortOrder,
            Callback<Response> callback);

    class Response {
        public AccountUsersList accountUsers;
        public BlueprintError error;
    }
}
