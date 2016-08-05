package com.xively;

import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;

/**
 * Provides access to SDK internal configuration values such as connection timeouts
 * and maximum automatic retry counts.
 *
 * The configuration becomes effective only by passing the custom configuration object
 * to the Authentication request service.
 */
public class XiSdkConfig {

    /**
     * Log levels for the Xively SDK's internal logger.
     */
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARNING, ERROR, OFF
    }

    public XiSdkConfig(){
        httpTimeout = Config.CONN_HTTP_TIMEOUT;
        httpReadtimeout = Config.CONN_HTTP_READTIMEOUT;
        mqttMaxTimeout = Config.CONN_MQTT_MAX_TIMEOUT;
        mqttDisconnectTimeout = Config.CONN_MQTT_DISCONNECT_TIMEOUT;
        mqttReconnectDelay = Config.CONN_MQTT_RECONNECT_DELAY;
        mqttMaxReconnect = Config.CONN_MQTT_MAX_RECONNECT;
        connectionKeepalive = Config.CONN_KEEPALIVE;

        logLevel = LMILog.getMinLogLevel();
    }

    /**
     * Timeout value in milliseconds for http and REST API calls of the Xively SDK.
     */
    public int httpTimeout;

    /**
     * Timeout value in milliseconds for read operations of http and REST API
     * calls of the Xively SDK.
     */
    public int httpReadtimeout;

    /**
     * The timeout value in milliseconds for the Xively Mqtt Connection.
     * If the connection cannot be established in at most the specified milliseconds,
     * the connection will eventually fail.
     */
    public int mqttMaxTimeout;

    /**
     * Timeout value for a single mqtt connection attempt.
     */
    public int mqttDisconnectTimeout;

    /**
     * Reconnect delay in milliseconds for the Xively Mqtt Connection.
     */
    public int mqttReconnectDelay;

    /**
     * Maximum retry count (maximum number of reconnect attempts on connection loss)
     * for the Xively Mqtt Connection.
     */
    public int mqttMaxReconnect;

    /**
     * Default keep alive value for network connections.
     */
    public int connectionKeepalive;

    /**
     * The current value of internal logger's log level.
     * By selecting log level OFF the SDK will not produce any LogCat output at all.
     */
    public LogLevel logLevel;

    /**
     * Resets all options to their default value in the current instance.
     */
    public void resetConfig(){
        httpTimeout = Config.DEFAULT_CONN_HTTP_TIMEOUT;
        httpReadtimeout = Config.DEFAULT_CONN_HTTP_READTIMEOUT;
        mqttMaxTimeout = Config.DEFAULT_CONN_MQTT_MAX_TIMEOUT;
        mqttDisconnectTimeout = Config.DEFAULT_CONN_MQTT_DISCONNECT_TIMEOUT;
        mqttReconnectDelay = Config.DEFAULT_CONN_MQTT_RECONNECT_DELAY;
        mqttMaxReconnect = Config.DEFAULT_CONN_MQTT_MAX_RECONNECT;
        connectionKeepalive = Config.DEFAULT_CONN_KEEPALIVE;

        LMILog.setMinLogLevel(LogLevel.INFO);
    }

    public XI_ENVIRONMENT environment;
    public enum XI_ENVIRONMENT {DEV, STAGE, DEMO, DEPLOY, LIVE}
}
