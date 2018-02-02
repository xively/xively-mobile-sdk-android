package com.xively.internal.rest.blueprint.credentialsCreate;


public class CredentialsCreate {
    public String accountId;
    public String entityId;
    public String entityType;

    @Override
    public String toString() {
        return "CredentialsCreate{" +
                "accountId='" + accountId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityType='" + entityType + '\'' +
                '}';
    }
}
