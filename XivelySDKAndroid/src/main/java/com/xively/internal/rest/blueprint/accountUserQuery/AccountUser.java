package com.xively.internal.rest.blueprint.accountUserQuery;


public class AccountUser {
    public String id;
    public String created;
    public String createdById;
    public String lastModified;
    public String lastModifiedById;
    public String version;
    public String accountId;
    public String userId;
    public String name;

    @Override
    public String toString() {
        return "AccountUser{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdById='" + createdById + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedById='" + lastModifiedById + '\'' +
                ", version='" + version + '\'' +
                ", accountId='" + accountId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
