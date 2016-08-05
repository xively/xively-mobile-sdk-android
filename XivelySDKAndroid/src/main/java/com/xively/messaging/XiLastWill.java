package com.xively.messaging;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Last will data for a messaging connection.
 */
public class XiLastWill {

    private String topic;
    private byte[] message;
    private XiMessaging.XiMessagingQoS qos;
    private boolean retain;

    private XiLastWill() {

    }

    public XiLastWill(String topic, byte[] message, XiMessaging.XiMessagingQoS qos, boolean retain){
        if (topic == null || topic.length() == 0) throw new InvalidParameterException("Topic needs to be filled");
        if (message == null || message.length == 0) throw new InvalidParameterException("Message needs to be filled");

        this.topic = topic;
        this.message = message;
        this.qos = qos;
        this.retain = retain;
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getMessage() {
        return message;
    }

    public XiMessaging.XiMessagingQoS getQos() {
        return qos;
    }

    public int getQosNumber() {
        switch (this.qos) {
            case AtMostOnce:
                return 0;

            case AtLeastOnce:
                return 1;

            case ExactlyOnce:
                return 2;

            default:
                return 0;
        }
    }

    public boolean getRetain() {
        return retain;
    }

    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof XiLastWill) ) return false;
        XiLastWill comparingLastWill = (XiLastWill)o;

        return this.topic.equals(comparingLastWill.getTopic()) &&
                Arrays.equals(this.message, comparingLastWill.getMessage()) &&
                this.qos == comparingLastWill.getQos() &&
                this.retain == comparingLastWill.retain;
    }

    @Override
    public String toString() {
        return
                "topic: " + topic + "\n" +
                        "message: " + message + "\n" +
                        "qos: " + qos + "\n" +
                        "retain: " + retain;
    }
}
