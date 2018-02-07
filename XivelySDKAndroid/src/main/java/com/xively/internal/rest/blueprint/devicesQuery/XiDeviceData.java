package com.xively.internal.rest.blueprint.devicesQuery;


import java.util.Arrays;

public class XiDeviceData {
    public String id;
    public String created;
    public String createdById;
    public String lastModified;
    public String lastModifiedById;
    public String ownedById;
    public String version;
    public String accountId;
    public String deviceTemplateId;
    public String organizationId;
    public String serialNumber;
    public String provisioningState;
    public String location;
    public String name;
    public String purchaseDate;
    public String connected;
    public XiChannelData[] channels;

    @Override
    public String toString() {
        return "XiDeviceData{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdById='" + createdById + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedById='" + lastModifiedById + '\'' +
                ", ownedById='" + ownedById + '\'' +
                ", version='" + version + '\'' +
                ", accountId='" + accountId + '\'' +
                ", deviceTemplateId='" + deviceTemplateId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", provisioningState='" + provisioningState + '\'' +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", connected='" + connected + '\'' +
                ", channels=" + Arrays.toString(channels) +
                '}';
    }
}
