package com.xively.messaging;

import java.util.List;

public interface XiDeviceUpdateCallback {

    void onDevicUpdateSuccess(XiDeviceInfo info);

    void onDeviceUpdateFailed();

}
