package com.xively.messaging;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by milgra on 26/07/16.
 */
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
    public LinkedTreeMap<String,Object>customFields;

    @Override
    public String toString() {

        return
                "organizationId: " + organizationId + "\n" +
                        "parentId: " + parentId + "\n" +
                        "organizationTemplateId: " + organizationTemplateId+ "\n" +
                        "name: " + name + "\n" +
                        "description: " + description+ "\n" +
                        "phoneNumber: " + phoneNumber + "\n" +
                        "address: " + address + "\n" +
                        "city: " + city + "\n" +
                        "state: " + state + "\n" +
                        "postalCode: " + postalCode + "\n" +
                        "countryCode: " + countryCode + "\n" +
                        "industry: " + industry + "\n" +
                        "organizationSize: " + organizaitonSize + "\n" +
                        "websiteAddress: " + websiteAddress + "\n" +
                        "customFields: " + customFields;
    }
}
