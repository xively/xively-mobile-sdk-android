package com.xively.messaging;

import java.util.List;

/**
 * Created by milgra on 26/07/16.
 */
public interface XiOrganizationListCallback {
    void onOrganizationListReceived(List<XiOrganizationInfo> organizationInfoList);

    void onOrganizationListFailed();

}
