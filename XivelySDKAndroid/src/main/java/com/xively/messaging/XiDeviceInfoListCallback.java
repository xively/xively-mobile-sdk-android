package com.xively.messaging;

import java.util.List;

public interface XiDeviceInfoListCallback {

    void onDeviceInfoListReceived(List<XiDeviceInfo> deviceInfoList);

    void onDeviceInfoListFailed();

}
