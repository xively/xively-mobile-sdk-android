package com.xively.messaging;


public interface XiEndUserCallback {
    void onEndUserReceived(XiEndUserInfo userInfo);

    void onEndUserFailed();
}
