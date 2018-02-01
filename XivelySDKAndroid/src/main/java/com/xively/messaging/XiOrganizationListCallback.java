package com.xively.messaging;

import java.util.List;


public interface XiOrganizationListCallback {
    void onOrganizationListReceived(List<XiOrganizationInfo> organizationInfoList);

    void onOrganizationListFailed();

}
