package com.xively.messaging;


public interface XiEndUserUpdateCallback {
    void onEndUserUpdateSuccess(XiEndUserInfo info);

    void onEndUserUpdateFailed();
}
