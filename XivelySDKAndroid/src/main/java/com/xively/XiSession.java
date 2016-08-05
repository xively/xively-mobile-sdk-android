package com.xively;

import com.xively.messaging.XiDeviceInfoCallback;
import com.xively.messaging.XiDeviceInfoListCallback;
import com.xively.messaging.XiDeviceUpdateCallback;
import com.xively.messaging.XiDeviceUpdateInfo;
import com.xively.messaging.XiEndUserCallback;
import com.xively.messaging.XiEndUserListCallback;
import com.xively.messaging.XiEndUserUpdateCallback;
import com.xively.messaging.XiEndUserUpdateInfo;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiOrganizationCallback;
import com.xively.messaging.XiOrganizationListCallback;
import com.xively.timeseries.XiTimeSeries;

import java.util.HashMap;

/**
 * This class provides access to Xively services, such as Xively Messaging, TimeSeries, Device Info etc.
 * A session instance can be used after valid authentication while the credentials and
 * tokens are living and valid.
 *
 * After logout it is expected that the session discarded and a new one is created on demand.
 */
public interface XiSession {

    /**
     * The Session's state is Active when the authentication and the corresponding token is valid.
     *
     * An Inactive Session is logged out or has an expired token.
     */
    enum State {Active, Inactive}

    /**
     * Returns the state of this session instance.
     *
     * @return A {@link com.xively.XiSession.State} value.
     */
    State getState();

    /**
     * Not implemented.
     */
    //void logout();

    /**
     * Returns a new MessagingCreator to provide an asynchronous creator for the
     * IoT Messaging Service.
     *
     * @return A {@link XiMessagingCreator} instance.
     */
    XiMessagingCreator requestMessaging();

    /**
     * Query all Xively organizations visible with the current end user credentials.
     *
     * @param callback Asynchronous callback for the organization query.
     */
    void requestXiOrganizationList(final XiOrganizationListCallback callback);

    /**
     * Query one Xivelyorganization.
     *
     * @param callback Asynchronous callback for the device info query.
     */

    void requestXiOrganization(String organizationId, final XiOrganizationCallback callback);

        /**
         * Query all Xively-enabled devices visible with the current end user credentials.
         *
         * @param callback Asynchronous callback for the device info query.
         */
    void requestXiDeviceInfoList(final XiDeviceInfoListCallback callback);

    /**
     * Query one Xively-enabled device.
     *
     * @param callback Asynchronous callback for the device info query.
     */
    void requestXiDeviceInfo(final String deviceId, final XiDeviceInfoCallback callback);

    /**
     * Update device in blueprint.
     *
     * @param callback Asynchronous callback for the device update query.
     */
    void requestXiDeviceUpdate(String deviceId, String version, HashMap<String,Object> deviceData, final XiDeviceUpdateCallback callback);

    /**
     * Query user list.
     *
     * @param callback Asynchronous callback for the device update query.
     */

    public void requestXiEndUserList(final XiEndUserListCallback callback);

    /**
     * Query user.
     *
     * @param callback Asynchronous callback for the device update query.
     */

    public void requestXiEndUser(String endUserId, final XiEndUserCallback callback);

    /**
     * Update user in blueprint.
     *
     * @param callback Asynchronous callback for the device update query.
     */

    public void requestXiEndUserUpdate(String userId, String version, HashMap<String,Object> userData, final XiEndUserUpdateCallback callback);

        /**
         * Access your Xively data stored in TimeSeries.
         * This call is synchronous.
         */

    XiTimeSeries timeSeries();
}
