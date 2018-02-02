package com.xively.messaging;

import com.google.gson.internal.LinkedTreeMap;


public class XiOrganizationInfo {
    public String organizationId;
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
    public String organizaitonSize;
    public String websiteAddress;
    public LinkedTreeMap<String, Object> customFields;

    @Override
    public String toString() {
        return "XiOrganizationInfo{" +
                "organizationId='" + organizationId + '\'' +
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
                ", organizaitonSize='" + organizaitonSize + '\'' +
                ", websiteAddress='" + websiteAddress + '\'' +
                ", customFields=" + customFields +
                '}';
    }
}
