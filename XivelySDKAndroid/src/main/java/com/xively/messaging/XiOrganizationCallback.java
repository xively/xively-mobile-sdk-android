package com.xively.messaging;

/**
 * Created by milgra on 27/07/16.
 */
public interface XiOrganizationCallback {
    void onOrganizationReceived(XiOrganizationInfo organizationInfo);
    void onOrganizationFailed();
}
