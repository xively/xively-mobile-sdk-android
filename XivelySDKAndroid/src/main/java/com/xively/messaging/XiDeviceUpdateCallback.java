package com.xively.messaging;


public interface XiDeviceUpdateCallback {

    void onDevicUpdateSuccess(XiDeviceInfo info);

    void onDeviceUpdateFailed();
}
