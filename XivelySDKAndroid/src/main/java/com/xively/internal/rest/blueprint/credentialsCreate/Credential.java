package com.xively.internal.rest.blueprint.credentialsCreate;


public class Credential {
    public String id;
    public String created;
    public String createdById;
    public String lastModified;
    public String lastModifiedById;
    public String version;
    public String accountId;
    public String entityId;
    public String entityType;
    public String secret;

    @Override
    public String toString() {
        return "Credential{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdById='" + createdById + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedById='" + lastModifiedById + '\'' +
                ", version='" + version + '\'' +
                ", accountId='" + accountId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
