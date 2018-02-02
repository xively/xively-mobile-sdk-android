package com.xively.messaging;


public interface XiDeviceInfoCallback {
    void onDeviceInfoReceived(XiDeviceInfo deviceInfo);

    void onDeviceInfoFailed();
}
