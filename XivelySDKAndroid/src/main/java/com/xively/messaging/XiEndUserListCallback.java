package com.xively.messaging;

import java.util.List;

/**
 * Created by milgra on 27/07/16.
 */
public interface XiEndUserListCallback {
    void onEndUserListReceived(List<XiEndUserInfo> userInfoList);
    void onEndUserListFailed();
}
