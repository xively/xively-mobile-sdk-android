package com.xively.messaging;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by milgra on 27/07/16.
 */
public class XiEndUserInfo {
    public String userId;
    public String emailAddress;
    public String version;
    public LinkedTreeMap<String,Object> customFields;

    @Override
    public String toString() {

        return
                "userId: " + userId+ "\n" +
                        "emailAddress: " + emailAddress+ "\n" +
                        "customFields: " + customFields;
    }
}
