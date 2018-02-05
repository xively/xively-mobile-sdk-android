package com.xively.internal.auth;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/*
 * {
 *   "expires":1517482896703,
 *   "renewalKey":"<renewalKey==>",
 *   "renewalExp":1518691296703,
 *   "cookieProperties":{
 *      "maxAge":1209600000,
 *      "domain":".xively.com",
 *      "secure":true,
 *      "isSecure":true,
 *      "httpOnly":true,
 *      "isHttpOnly":true
 *   },
 *   "id":"<id>",
 *   "userId":"<userId>",
 *   "accountId":"<accountId>",
 *   "roles":[
 *
 *   ],
 *   "cert":"<certId>"
 * }
 */

public class XivelyJWT {

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("expires")
    private long expires;

    @SerializedName("renewalKey")
    private String renewalKey;

    @SerializedName("accountId")
    private String accountId;

    @SerializedName("cert")
    private String cert;

    @SerializedName("roles")
    private List<Map<String, String>> roles;

    // Empty Constructor required by Gson
    public XivelyJWT() {
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getExpires() {
        return expires;
    }

    public String getRenewalKey() {
        return renewalKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCert() {
        return cert;
    }

    public List<Map<String, String>> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "XivelyJWT{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", expires='" + expires + '\'' +
                ", renewalKey='" + renewalKey + '\'' +
                ", accountId='" + accountId + '\'' +
                ", cert='" + cert + '\'' +
                ", roles=" + roles +
                '}';
    }
}
