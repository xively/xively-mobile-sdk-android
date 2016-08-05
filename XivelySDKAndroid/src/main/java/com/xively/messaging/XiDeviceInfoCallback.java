package com.xively.messaging;

/**
 * Created by milgra on 26/07/16.
 */
public interface XiDeviceInfoCallback {
    void onDeviceInfoReceived(XiDeviceInfo deviceInfo);
    void onDeviceInfoFailed();
}
