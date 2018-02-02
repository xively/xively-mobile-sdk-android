package com.xively.messaging;


public interface XiOrganizationCallback {
    void onOrganizationReceived(XiOrganizationInfo organizationInfo);

    void onOrganizationFailed();
}
