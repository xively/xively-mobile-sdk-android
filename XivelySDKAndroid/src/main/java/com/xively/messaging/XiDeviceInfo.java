package com.xively.messaging;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class XiDeviceInfo {
    public enum ProvisioningStateEnum {defined, activated, associated, reserved}

    public enum PersistenceTypeEnum {simple, timeSeries}

    public String deviceId;
    public String serialNumber;
    public ProvisioningStateEnum provisioningState;
    public String deviceVersion;
    public String deviceLocation;
    public String deviceName;
    public String purchaseDate;//FIXME: parse date
    public ArrayList<XiDeviceChannel> deviceChannels;
    public LinkedTreeMap<String, Object> customFields;

    @Override
    public String toString() {
        String deviceChannelsString = "";
        if (deviceChannels != null && deviceChannels.size() > 0) {
            for (XiDeviceChannel deviceChannel : deviceChannels) {
                deviceChannelsString += "Channel id: " + deviceChannel.channelId + " \n";
                deviceChannelsString += "Channel type: " + deviceChannel.persistenceType.toString() + "\n ";
            }
        } else {
            deviceChannelsString = "N/A";
        }

        return "XiDeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", provisioningState=" + provisioningState +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", deviceLocation='" + deviceLocation + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", deviceChannels=" + deviceChannelsString +
                ", customFields=" + customFields +
                '}';
    }
}
