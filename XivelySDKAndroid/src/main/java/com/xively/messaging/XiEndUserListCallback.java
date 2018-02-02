package com.xively.messaging;

import java.util.List;


public interface XiEndUserListCallback {
    void onEndUserListReceived(List<XiEndUserInfo> userInfoList);

    void onEndUserListFailed();
}
