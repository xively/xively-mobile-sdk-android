package com.xively.internal.auth;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/*
 * {
 *   "expires":1517482896703,
 *   "renewalKey":"D7j2ktfL3uvzchbCpHYH8A==",
 *   "renewalExp":1518691296703,
 *   "cookieProperties":{
 *      "maxAge":1209600000,
 *      "domain":".xively.com",
 *      "secure":true,
 *      "isSecure":true,
 *      "httpOnly":true,
 *      "isHttpOnly":true
 *   },
 *   "id":"adf09ec5-0c89-4ab7-9668-2f3debe5fd5a",
 *   "userId":"2703e7d6-612f-428b-a56d-c96b0cb7acff",
 *   "accountId":"d95ee4d1-45fb-4061-b3fb-f2651158b416",
 *   "roles":[
 *
 *   ],
 *   "cert":"0efb9b3e-d210-44f2-9bec-31243e132a62"
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

    public XivelyJWT(String id, String userId, long expires, String renewalKey, String accountId, String cert, List<Map<String, String>> roles) {
        this.id = id;
        this.userId = userId;
        this.expires = expires;
        this.renewalKey = renewalKey;
        this.accountId = accountId;
        this.cert = cert;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getRenewalKey() {
        return renewalKey;
    }

    public void setRenewalKey(String renewalKey) {
        this.renewalKey = renewalKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public List<Map<String, String>> getRoles() {
        return roles;
    }

    public void setRoles(List<Map<String, String>> roles) {
        this.roles = roles;
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
