package com.xively.internal.device;

/**
 * Utility class to provide basic information about the end user's mobile device.
 */
public interface DeviceInfo {

    /**
     * Generates a unique user id which will remain the same as long as the application is not
     * uninstalled or reinstalled, respectively application is data is not cleared.
     *
     * @return A unique user id.
     */
	String getUUId();
}
