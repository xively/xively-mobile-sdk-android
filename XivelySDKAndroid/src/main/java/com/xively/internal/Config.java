package com.xively.internal;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

/**
 * @internal
 */
public abstract class Config {

    private static final String XIVELY_STAGE_HOST = ".stage.xively.us";
    private static final String XIVELY_DEV_HOST = ".dev.xively.io";
    private static final String XIVELY_DEMO_HOST = ".com.xively.demo.xively.com";
    private static final String XIVELY_DEPLOY_HOST = ".deploy.xively.com";
    private static final String XIVELY_LIVE_HOST = ".xively.com";

    private static String XIVELY_HOST = XIVELY_LIVE_HOST;

    public static final boolean CONN_USE_SSL                = true;
    private static final String  CONN_XI_MQTT_HOST           = "broker";
    public static final int     CONN_XI_MQTT_PORT           = 1883;
    public static final int     CONN_XI_MQTT_SECURE_PORT    = 8883;
    public static final int     CONN_XI_MQTT_QOS            = 1;
    public static final int     CONN_XI_MQTT_QOS_LOW        = 0;
    public static int           CONN_HTTP_TIMEOUT           = 15000;//msec
    public static int           CONN_HTTP_READTIMEOUT       = 15000;//msec
    public static int           CONN_MQTT_MAX_TIMEOUT       = 15000;//msec
    public static int           CONN_MQTT_DISCONNECT_TIMEOUT= 2000;//msec
    public static int           CONN_MQTT_RECONNECT_DELAY   = 2000;//msec
    public static int           CONN_MQTT_MAX_RECONNECT     = 3;//count
    public static int           CONN_KEEPALIVE              = 30;//sec

    private static final String BLUEPRINT_WS_ENDPOINT = "blueprint";
    private static final String AUTH_WS_ENDPOINT = "id";
    private static final String PROVISION_WS_ENDPOINT = "provision";
    private static final String TIMESERIES_WS_ENDPOINT = "timeseries";

    public static final String ACCESS_WS_ENDPOINT = "access" + XIVELY_HOST;

    //default values
    public static final int DEFAULT_CONN_HTTP_TIMEOUT			= 15000;//msec
    public static final int DEFAULT_CONN_HTTP_READTIMEOUT 	    = 15000;//msec
    public static final int DEFAULT_CONN_MQTT_MAX_TIMEOUT       = 15000;//msec
    public static final int DEFAULT_CONN_MQTT_DISCONNECT_TIMEOUT= 2000;//msec
    public static final int DEFAULT_CONN_MQTT_RECONNECT_DELAY   = 2000;//msec
    public static final int DEFAULT_CONN_MQTT_MAX_RECONNECT 	= 3;//count
    public static final int DEFAULT_CONN_KEEPALIVE 	    	    = 30;//sec

    public static void setEnv(XiSdkConfig.XI_ENVIRONMENT environment){
        switch (environment){

            case DEV:
                XIVELY_HOST = XIVELY_DEV_HOST;
                break;
            case DEMO:
                XIVELY_HOST = XIVELY_DEMO_HOST;
                break;
            case DEPLOY:
                XIVELY_HOST = XIVELY_DEPLOY_HOST;
                break;
            case LIVE:
                XIVELY_HOST = XIVELY_LIVE_HOST;
                break;
            case STAGE:
            default:
                XIVELY_HOST = XIVELY_STAGE_HOST;
        }
    }

    public static String getXivelyHost(){
        return XIVELY_HOST;
    }

    public static String xi_mqtt_host(){
        return CONN_XI_MQTT_HOST + XIVELY_HOST;
    }

    public static String blueprint_endpoint(){
        return BLUEPRINT_WS_ENDPOINT + XIVELY_HOST;
    }

    public static String auth_endpoint(){
        return AUTH_WS_ENDPOINT + XIVELY_HOST;
    }

    public static String provision_endpoint(){
        return PROVISION_WS_ENDPOINT + XIVELY_HOST;
    }

    public static String timeseries_endpoint(){
        return TIMESERIES_WS_ENDPOINT + XIVELY_HOST;
    }

    public static void setConfig(XiSdkConfig customConfig){
        CONN_HTTP_TIMEOUT = customConfig.httpTimeout;
        CONN_HTTP_READTIMEOUT = customConfig.httpReadtimeout;
        CONN_MQTT_MAX_TIMEOUT = customConfig.mqttMaxTimeout;
        CONN_MQTT_DISCONNECT_TIMEOUT = customConfig.mqttDisconnectTimeout;
        CONN_MQTT_RECONNECT_DELAY = customConfig.mqttReconnectDelay;
        CONN_MQTT_MAX_RECONNECT = customConfig.mqttMaxReconnect;
        CONN_KEEPALIVE = customConfig.connectionKeepalive;
        LMILog.setMinLogLevel(customConfig.logLevel);

        if (customConfig.environment != null){
            setEnv(customConfig.environment);
            DependencyInjector.get().resetInstances();
        }
    }

    /**
     * Reset all settings to default value.
     */
    public static void resetConfig(){
        Config.CONN_HTTP_TIMEOUT = Config.DEFAULT_CONN_HTTP_TIMEOUT;
        Config.CONN_HTTP_READTIMEOUT = Config.DEFAULT_CONN_HTTP_READTIMEOUT;
        Config.CONN_MQTT_MAX_TIMEOUT = Config.DEFAULT_CONN_MQTT_MAX_TIMEOUT;
        Config.CONN_MQTT_DISCONNECT_TIMEOUT = Config.DEFAULT_CONN_MQTT_DISCONNECT_TIMEOUT;
        Config.CONN_MQTT_RECONNECT_DELAY = Config.DEFAULT_CONN_MQTT_RECONNECT_DELAY;
        Config.CONN_MQTT_MAX_RECONNECT = Config.DEFAULT_CONN_MQTT_MAX_RECONNECT;
        Config.CONN_KEEPALIVE = Config.DEFAULT_CONN_KEEPALIVE;
    }

}
