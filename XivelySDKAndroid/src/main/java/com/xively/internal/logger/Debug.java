package com.xively.internal.logger;

import com.xively.XiSdkConfig;

class Debug {

    private static final String TAG = "Debug";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    public static void DebugBreak() {
        log.l(XiSdkConfig.LogLevel.INFO, "DebugBreak!");
    }

}
