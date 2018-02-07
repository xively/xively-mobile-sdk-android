package com.xively.internal.rest.blueprint.organizationsQuery;


public class XiOrganizationData {
    public String id;
    public String created;
    public String createdById;
    public String lastModified;
    public String lastModifiedById;
    public String version;
    public String accountId;
    public String parentId;
    public String organizationTemplateId;
    public String name;
    public String description;
    public String phoneNumber;
    public String address;
    public String city;
    public String state;
    public String postalCode;
    public String countryCode;
    public String industry;
    public String organizationSize;
    public String websiteAddress;

    @Override
    public String toString() {
        return "XiOrganizationData{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", createdById='" + createdById + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedById='" + lastModifiedById + '\'' +
                ", version='" + version + '\'' +
                ", accountId='" + accountId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", organizationTemplateId='" + organizationTemplateId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", industry='" + industry + '\'' +
                ", organizationSize='" + organizationSize + '\'' +
                ", websiteAddress='" + websiteAddress + '\'' +
                '}';
    }
}
