package com.xively.messaging;

/**
 * Created by milgra on 29/07/16.
 */
public interface XiEndUserUpdateCallback {
    void onEndUserUpdateSuccess(XiEndUserInfo info);

    void onEndUserUpdateFailed();

}
