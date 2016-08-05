package com.xively.messaging;
import com.xively.XiException;
import com.xively.XiServiceCreator;
import com.xively.XiServiceCreatorCallback;

/**
 * Service Creator for {@link XiMessaging}.
 */
public abstract class XiMessagingCreator extends XiServiceCreator<XiMessaging> {

    /**
     * Asynchronous call to create a Xively Messaging Service instance. The clean session is
     * set to true, and there is no last will set.
     * The service will be returned as a parameter of the serviceCreated event
     * of the {@link XiServiceCreatorCallback}.
     *
     * Service Creator Callback listeners must registered before this method is called.
     */
    public abstract void createMessaging();

    /**
     * Asynchronous call to create a Xively Messaging Service instance. The clean session can be
     * set, and there is no last will.
     * The service will be returned as a parameter of the serviceCreated event
     * of the {@link XiServiceCreatorCallback}.
     *
     * Service Creator Callback listeners must registered before this method is called.
     */
    public abstract void createMessaging(boolean cleanSession);

    /**
     * Asynchronous call to create a Xively Messaging Service instance. Both the clean session and
     * the last will can be set.
     * The service will be returned as a parameter of the serviceCreated event
     * of the {@link XiServiceCreatorCallback}.
     *
     * Service Creator Callback listeners must registered before this method is called.
     */
    public abstract void createMessaging(boolean cleanSession, XiLastWill lastWill);

}