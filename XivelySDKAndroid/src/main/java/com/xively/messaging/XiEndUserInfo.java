package com.xively.messaging;

import com.google.gson.internal.LinkedTreeMap;


public class XiEndUserInfo {
    public String userId;
    public String emailAddress;
    public String version;
    public LinkedTreeMap<String, Object> customFields;

    @Override
    public String toString() {
        return "XiEndUserInfo{" +
                "userId='" + userId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", version='" + version + '\'' +
                ", customFields=" + customFields +
                '}';
    }
}
