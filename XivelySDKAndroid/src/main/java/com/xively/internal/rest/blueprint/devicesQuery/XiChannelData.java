package com.xively.internal.rest.blueprint.devicesQuery;


public class XiChannelData {
    public String channelTemplateId;
    public String channelTemplateName;
    public String persistenceType;
    public String channel;

    @Override
    public String toString() {
        return "XiChannelData{" +
                "channelTemplateId='" + channelTemplateId + '\'' +
                ", channelTemplateName='" + channelTemplateName + '\'' +
                ", persistenceType='" + persistenceType + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
