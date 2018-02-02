package com.xively.internal.rest.blueprint.endUserQuery;


public class EndUser {
    public String id;
    public String created;
    public String createdById;
    public String lastModified;
    public String lastModifiedById;
    public String version;
    public String accountId;
    public String organizationId;
    public String userId;
    public String emailAddress;

    @Override
    public String toString() {
        return "EndUser{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdById='" + createdById + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedById='" + lastModifiedById + '\'' +
                ", version='" + version + '\'' +
                ", accountId='" + accountId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", userId='" + userId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
