package com.xively.internal;

import com.google.gson.internal.LinkedTreeMap;
import com.xively.XiSession;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.logger.LMILog;
import com.xively.internal.messaging.XiMessagingCreatorImpl;
import com.xively.internal.rest.blueprint.GetDevice;
import com.xively.internal.rest.blueprint.GetDevices;
import com.xively.internal.rest.blueprint.GetEndUser;
import com.xively.internal.rest.blueprint.GetEndUsers;
import com.xively.internal.rest.blueprint.GetOrganization;
import com.xively.internal.rest.blueprint.GetOrganizations;
import com.xively.internal.rest.blueprint.PutDevice;
import com.xively.internal.rest.blueprint.PutEndUser;
import com.xively.internal.timeseries.XiTimeSeriesImpl;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiDeviceInfoCallback;
import com.xively.messaging.XiDeviceInfoListCallback;
import com.xively.messaging.XiDeviceUpdateCallback;
import com.xively.messaging.XiEndUserCallback;
import com.xively.messaging.XiEndUserInfo;
import com.xively.messaging.XiEndUserListCallback;
import com.xively.messaging.XiEndUserUpdateCallback;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiOrganizationCallback;
import com.xively.messaging.XiOrganizationInfo;
import com.xively.messaging.XiOrganizationListCallback;
import com.xively.timeseries.XiTimeSeries;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XiSessionImpl implements XiSession {
    private static final String TAG = "XiSession";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private XiMqttConnectionPool mqttConnectionPool = null;
    private XivelyAccount account = null;

    public XiSessionImpl() {
    }

    private State state = State.Inactive;

    public void setXivelyAccount(XivelyAccount account) {
        this.account = account;
        state = State.Active;
    }

    /**
     * Only for unit testing purposes.
     *
     * @param mqttConnectionPool Custom connection pool instance.
     */
    public void setMqttConnectionPool(XiMqttConnectionPool mqttConnectionPool) {
        this.mqttConnectionPool = mqttConnectionPool;
    }

    public XiMqttConnectionPool getMqttConnectionPool() {
        if (mqttConnectionPool == null) {
            mqttConnectionPool = DependencyInjector.get().createMqttConnectionPool(account);
        }
        return mqttConnectionPool;
    }

    @Override
    public State getState() {
        return state;
    }

    public void logout() {
        //TODO: implement
        log.t("Logout requested.");
        state = State.Inactive;
        throw new UnsupportedOperationException();
    }

    public String getLongLivingToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XiMessagingCreator requestMessaging() {
        log.t("Messaging requested.");
        return new XiMessagingCreatorImpl(getMqttConnectionPool());
    }

    private final ArrayList<XiEndUserInfo> aggregatedEndUsersResult = new ArrayList<>();

    public void requestXiEndUserList(final XiEndUserListCallback callback) {

        aggregatedEndUsersResult.clear();
        requestMoreXiEndUserList(callback, 1);
    }

    public void requestMoreXiEndUserList(final XiEndUserListCallback callback, int page) {

        final ArrayList<XiEndUserInfo> aggregatedResult = new ArrayList<>();

        final Callback<GetEndUsers.Response> blueprintCallback = new Callback<GetEndUsers.Response>() {
            @Override
            public void onResponse(Call<GetEndUsers.Response> call, Response<GetEndUsers.Response> response) {
                log.i("Get end user list success.");
                GetEndUsers.Response getEndUsersResponse = response.body();

                if (getEndUsersResponse == null ||
                        getEndUsersResponse.endUsers == null ||
                        getEndUsersResponse.error != null) {
                    onFailure(null, null);
                    return;
                }

                parseEndUserResults(aggregatedEndUsersResult, getEndUsersResponse.endUsers);

                LinkedTreeMap<String, Object> metaMap = (LinkedTreeMap<String, Object>) getEndUsersResponse.endUsers.get("meta");

                Double page = (Double) metaMap.get("page");
                Double pageSize = (Double) metaMap.get("pageSize");
                Double count = (Double) metaMap.get("count");

                if (page * pageSize < count) {
                    requestMoreXiEndUserList(callback, page.intValue() + 1);
                } else callback.onEndUserListReceived(aggregatedEndUsersResult);
            }

            @Override
            public void onFailure(Call<GetEndUsers.Response> call, Throwable t) {
                log.w("Failed to get end user list.");
//                log.w(""+new String(((TypedByteArray) retrofitError.getResponse().getBody()).getBytes()));
                callback.onEndUserListFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getEndUserList(
                account.getClientId(), account.getUserName(), page, 999, blueprintCallback);
    }

    public void requestXiEndUser(String endUserId, final XiEndUserCallback callback) {

        final Callback<GetEndUser.Response> blueprintCallback = new Callback<GetEndUser.Response>() {
            @Override
            public void onResponse(Call<GetEndUser.Response> call, Response<GetEndUser.Response> response) {
                log.i("Get end user success.");
                GetEndUser.Response getEndUserResponse = response.body();

                if (getEndUserResponse == null || getEndUserResponse.error != null) {
                    onFailure(null, null);
                    return;
                }

                XiEndUserInfo info = parseEndUser(getEndUserResponse.endUser);
                callback.onEndUserReceived(info);
            }

            @Override
            public void onFailure(Call<GetEndUser.Response> call, Throwable t) {
                log.w("Failed to get end user");
                callback.onEndUserFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getEndUser(endUserId, blueprintCallback);
    }

    public void requestXiEndUserUpdate(String userId, String version, HashMap<String, Object> userData, final XiEndUserUpdateCallback callback) {

        final Callback<PutEndUser.Response> updateCallback = new Callback<PutEndUser.Response>() {
            @Override
            public void onResponse(Call<PutEndUser.Response> call, Response<PutEndUser.Response> response) {
                log.i("Update end user success.");
                PutEndUser.Response putEndUserResponse = response.body();

                if (putEndUserResponse == null || putEndUserResponse.error != null) {
                    onFailure(call, null);
                    return;
                }
                XiEndUserInfo info = parseEndUser(putEndUserResponse.endUser);
                callback.onEndUserUpdateSuccess(info);
            }

            @Override
            public void onFailure(Call<PutEndUser.Response> call, Throwable t) {
                log.w("Failed to update end user");
                callback.onEndUserUpdateFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().putEndUser(userId, version, userData, updateCallback);
    }

    private final ArrayList<XiOrganizationInfo> aggregatedOrganizationsResult = new ArrayList<>();

    public void requestXiOrganizationList(final XiOrganizationListCallback callback) {
        aggregatedOrganizationsResult.clear();
        requestMoreXiOrganizationList(callback, 1);
    }

    public void requestMoreXiOrganizationList(final XiOrganizationListCallback callback, int startpage) {

        final Callback<GetOrganizations.Response> blueprintCallback = new Callback<GetOrganizations.Response>() {
            @Override
            public void onResponse(Call<GetOrganizations.Response> call, Response<GetOrganizations.Response> response) {
                log.i("Get organization list success.");
                GetOrganizations.Response getOrganizationResponse = response.body();

                if (getOrganizationResponse == null ||
                        getOrganizationResponse.organizations == null ||
                        getOrganizationResponse.error != null) {
                    onFailure(call, null);
                    return;
                }

                parseOrganizationsResults(aggregatedOrganizationsResult, getOrganizationResponse.organizations);

                LinkedTreeMap<String, Object> metaMap = (LinkedTreeMap<String, Object>) getOrganizationResponse.organizations.get("meta");

                Double page = (Double) metaMap.get("page");
                Double pageSize = (Double) metaMap.get("pageSize");
                Double count = (Double) metaMap.get("count");

                if (page * pageSize < count) {
                    requestMoreXiOrganizationList(callback, page.intValue() + 1);
                } else callback.onOrganizationListReceived(aggregatedOrganizationsResult);
            }

            @Override
            public void onFailure(Call<GetOrganizations.Response> call, Throwable t) {
                log.w("Failed to get organization list.");
                callback.onOrganizationListFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getOrganizations(
                account.getClientId(), null, null, null, startpage, 999, blueprintCallback);
    }

    public void requestXiOrganization(String organizationId, final XiOrganizationCallback callback) {

        final Callback<GetOrganization.Response> blueprintCallback = new Callback<GetOrganization.Response>() {
            @Override
            public void onResponse(Call<GetOrganization.Response> call, Response<GetOrganization.Response> response) {
                log.i("Get organization success." + response);
                GetOrganization.Response getOrganizationResponse = response.body();

                if (getOrganizationResponse == null || getOrganizationResponse.error != null) {
                    onFailure(call, null);
                    return;
                }
                XiOrganizationInfo info = parseOrganization(getOrganizationResponse.organization);
                callback.onOrganizationReceived(info);
            }

            @Override
            public void onFailure(Call<GetOrganization.Response> call, Throwable t) {
                log.w("Failed to get organization");
                callback.onOrganizationFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getOrganization(organizationId, blueprintCallback);
    }

    private final ArrayList<XiDeviceInfo> aggregatedDevicesResult = new ArrayList<>();

    public void requestXiDeviceInfoList(final XiDeviceInfoListCallback callback) {

        aggregatedDevicesResult.clear();
        requestMoreXiDeviceInfoList(callback, 1);
    }

    public void requestMoreXiDeviceInfoList(final XiDeviceInfoListCallback callback, int startpage) {

        final Callback<GetDevices.Response> blueprintCallback = new Callback<GetDevices.Response>() {
            @Override
            public void onResponse(Call<GetDevices.Response> call, Response<GetDevices.Response> response) {
                log.i("Get device info list success.");
                GetDevices.Response getDevicesResponse = response.body();

                if (getDevicesResponse == null ||
                        getDevicesResponse.devices == null ||
                        getDevicesResponse.error != null) {
                    onFailure(call, null);
                    return;
                }

                parseDeviceInfoResults(aggregatedDevicesResult, getDevicesResponse.devices);

                LinkedTreeMap<String, Object> metaMap = (LinkedTreeMap<String, Object>) getDevicesResponse.devices.get("meta");

                Double page = (Double) metaMap.get("page");
                Double pageSize = (Double) metaMap.get("pageSize");
                Double count = (Double) metaMap.get("count");

                if (page * pageSize < count) {
                    requestMoreXiDeviceInfoList(callback, page.intValue() + 1);
                } else callback.onDeviceInfoListReceived(aggregatedDevicesResult);
            }

            @Override
            public void onFailure(Call<GetDevices.Response> call, Throwable t) {
                log.w("Failed to get devices info list.");
                callback.onDeviceInfoListFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getDevices(
                account.getClientId(), null, null, null, startpage, 999, blueprintCallback);
    }


    public void requestXiDeviceInfo(String deviceId, final XiDeviceInfoCallback callback) {

        final Callback<GetDevice.Response> blueprintCallback = new Callback<GetDevice.Response>() {
            @Override
            public void onResponse(Call<GetDevice.Response> call, Response<GetDevice.Response> response) {
                log.i("Get device success.");
                GetDevice.Response getDeviceResponse = response.body();

                if (getDeviceResponse == null || getDeviceResponse.error != null) {
                    onFailure(call, null);
                    return;
                }
                XiDeviceInfo info = parseDeviceInfo(getDeviceResponse.device);
                callback.onDeviceInfoReceived(info);
            }

            @Override
            public void onFailure(Call<GetDevice.Response> call, Throwable t) {
                log.w("Failed to get device");
                callback.onDeviceInfoFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().getDevice(deviceId, blueprintCallback);
    }

    public void requestXiDeviceUpdate(String deviceId, String version, HashMap<String, Object> deviceData, final XiDeviceUpdateCallback callback) {

        final Callback<PutDevice.Response> updateCallback = new Callback<PutDevice.Response>() {
            @Override
            public void onResponse(Call<PutDevice.Response> call, Response<PutDevice.Response> response) {
                log.i("Put device success.");
                PutDevice.Response putDeviceResponse = response.body();

                if (putDeviceResponse == null || putDeviceResponse.error != null) {
                    onFailure(call, null);
                    return;
                }
                XiDeviceInfo info = parseDeviceInfo(putDeviceResponse.device);
                callback.onDevicUpdateSuccess(info);
            }

            @Override
            public void onFailure(Call<PutDevice.Response> call, Throwable t) {
                log.w("Failed to update device");
                callback.onDeviceUpdateFailed();
            }
        };

        DependencyInjector.get().blueprintWebServices().putDevice(deviceId, version, deviceData, updateCallback);
    }

    @Override
    public XiTimeSeries timeSeries() {
        return new XiTimeSeriesImpl();
    }

    private XiDeviceInfo parseDeviceInfo(LinkedTreeMap<String, Object> deviceMap) {
        XiDeviceInfo deviceInfo = new XiDeviceInfo();

        deviceInfo.deviceId = (String) deviceMap.get("id");
        deviceInfo.serialNumber = (String) deviceMap.get("serialNumber");
        deviceInfo.provisioningState = XiDeviceInfo.ProvisioningStateEnum.valueOf((String) deviceMap.get("provisioningState"));
        deviceInfo.deviceVersion = (String) deviceMap.get("version");
        deviceInfo.deviceLocation = (String) deviceMap.get("location");
        deviceInfo.deviceName = (String) deviceMap.get("name");
        ;
        deviceInfo.purchaseDate = (String) deviceMap.get("purchaseDate");
        deviceInfo.customFields = deviceMap;

        ArrayList<Object> rawchannels = (ArrayList<Object>) deviceMap.get("channels");

        if (rawchannels != null && rawchannels.size() > 0) {
            ArrayList<XiDeviceChannel> channels = new ArrayList<>();

            for (Object rawChannelData : rawchannels) {
                LinkedTreeMap<String, Object> channelData = (LinkedTreeMap<String, Object>) rawChannelData;
                XiDeviceChannel channel = new XiDeviceChannel();
                channel.channelId = (String) channelData.get("channel");
                channel.persistenceType =
                        XiDeviceInfo.PersistenceTypeEnum.valueOf((String) channelData.get("persistenceType"));

                channels.add(channel);
            }
            deviceInfo.deviceChannels = channels;

        } else {
            deviceInfo.deviceChannels = null;
        }

        return deviceInfo;
    }

    private void parseDeviceInfoResults(final ArrayList<XiDeviceInfo> result, HashMap<String, Object> devices) {
        ArrayList<Object> devicesList = (ArrayList<Object>) devices.get("results");
        for (Object data : devicesList) {
            XiDeviceInfo deviceInfo = parseDeviceInfo((LinkedTreeMap<String, Object>) data);
            result.add(deviceInfo);
        }
    }

    private XiOrganizationInfo parseOrganization(LinkedTreeMap<String, Object> organizationMap) {

        XiOrganizationInfo organizationInfo = new XiOrganizationInfo();

        organizationInfo.organizationId = (String) organizationMap.get("id");
        organizationInfo.parentId = (String) organizationMap.get("parentId");
        organizationInfo.organizationTemplateId = (String) organizationMap.get("organizationTemplateId");
        organizationInfo.name = (String) organizationMap.get("name");
        organizationInfo.customFields = organizationMap;

        return organizationInfo;
    }

    private void parseOrganizationsResults(final ArrayList<XiOrganizationInfo> result, HashMap<String, Object> devices) {
        ArrayList<Object> organizationsList = (ArrayList<Object>) devices.get("results");
        for (Object data : organizationsList) {
            XiOrganizationInfo organizationInfo = parseOrganization((LinkedTreeMap<String, Object>) data);
            result.add(organizationInfo);
        }
    }

    private XiEndUserInfo parseEndUser(LinkedTreeMap<String, Object> endUserMap) {
        XiEndUserInfo info = new XiEndUserInfo();
        info.userId = (String) endUserMap.get("id");
        info.emailAddress = (String) endUserMap.get("emailAddress");
        info.customFields = endUserMap;
        info.version = (String) endUserMap.get("version");

        return info;
    }

    private void parseEndUserResults(final ArrayList<XiEndUserInfo> result, HashMap<String, Object> endUsers) {
        ArrayList<Object> endUserList = (ArrayList<Object>) endUsers.get("results");
        for (Object data : endUserList) {
            XiEndUserInfo info = parseEndUser((LinkedTreeMap<String, Object>) data);
            result.add(info);
        }
    }
}
