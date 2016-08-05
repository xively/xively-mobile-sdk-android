package com.xively.internal.messaging;

import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.DependencyInjector;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiMessagingStateListener;

public class XiMessagingCreatorImpl extends XiMessagingCreator {

    private XiMqttConnectionPool connectionPool;

    public XiMessagingCreatorImpl(XiMqttConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public void createMessaging() {
        this.createMessaging(true);
    }

    @Override
    public void createMessaging(boolean cleanSession) {
        this.createMessaging(cleanSession, null);
    }

    @Override
    public void createMessaging(boolean cleanSession, XiLastWill lastWill) {
        final XiMessaging messagingService =
                DependencyInjector.get().createXiMessaging(connectionPool);

        messagingService.addStateListener(new XiMessagingStateListener() {
            @Override
            public void onStateChanged(XiMessaging.State newState) {
                if (newState == XiMessaging.State.Running) {
                    messagingService.removeStateListener(this);
                    serviceCreated(messagingService);
                }
            }

            @Override
            public void onError() {
                messagingService.removeStateListener(this);
                serviceCreateFailed();
            }
        });

        try {
            ((XiMessagingImpl) messagingService).init(cleanSession, lastWill);
        }catch (IllegalArgumentException exception) {
            serviceCreateFailed();
        }
    }

}
