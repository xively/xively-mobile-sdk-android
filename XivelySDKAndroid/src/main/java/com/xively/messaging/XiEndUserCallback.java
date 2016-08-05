package com.xively.messaging;

/**
 * Created by milgra on 27/07/16.
 */
public interface XiEndUserCallback {
    void onEndUserReceived(XiEndUserInfo userInfo);
    void onEndUserFailed();
}
