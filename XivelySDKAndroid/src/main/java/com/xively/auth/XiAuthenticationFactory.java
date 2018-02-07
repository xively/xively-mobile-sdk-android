package com.xively.auth;

import android.content.Context;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.auth.XiAuthenticationImpl;

public class XiAuthenticationFactory {

    /**
     * @param context
     * @return
     */
    public static XiAuthentication createAuthenticationService(Context context) {
        DependencyInjector.get().initDependencies(context);

        return new XiAuthenticationImpl();
    }

    /**
     * @param context
     * @param customConfig
     * @return
     */
    public static XiAuthentication createAuthenticationService(Context context, XiSdkConfig customConfig) {
        Config.setConfig(customConfig);

        return createAuthenticationService(context);
    }
}
