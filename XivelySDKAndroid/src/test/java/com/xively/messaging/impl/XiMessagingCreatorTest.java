package com.xively.messaging.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import com.xively.XiServiceCreatorCallback;
import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.DependencyInjector;
import com.xively.internal.messaging.XiMessagingCreatorImpl;
import com.xively.internal.messaging.XiMessagingImpl;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingStateListener;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class XiMessagingCreatorTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private XiMessagingImpl mockMessaging;
    @Mock
    private XiMqttConnectionPool mockConnectionPool;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        when(mockDependencyInjector.createXiMessaging(mockConnectionPool))
                .thenReturn(mockMessaging);
        DependencyInjector.setInstance(mockDependencyInjector);
    }

    public void testCreatorCreatesMessagingObject()
            throws IllegalArgumentException {
        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        messagingCreator.createMessaging();

        verify(mockMessaging, times(1)).addStateListener(Matchers.<XiMessagingStateListener>anyObject());
        verify(mockMessaging, times(1)).init(true, null);
    }

    public void testCreatorCreatesMessagingObjectWithCleanSession()
            throws IllegalArgumentException {
        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        messagingCreator.createMessaging(false);

        verify(mockMessaging, times(1)).addStateListener(Matchers.<XiMessagingStateListener>anyObject());
        verify(mockMessaging, times(1)).init(false, null);
    }

    public void testCreatorCreatesMessagingObjectWithCleanSessionAndLastWill()
            throws IllegalArgumentException {
        XiLastWill lastWill = new XiLastWill("aaa", "bbb".getBytes(),
                XiMessaging.XiMessagingQoS.AtLeastOnce, false);
        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        messagingCreator.createMessaging(false, lastWill);

        verify(mockMessaging, times(1)).addStateListener(Matchers.<XiMessagingStateListener>anyObject());
        verify(mockMessaging, times(1)).init(false, lastWill);
    }

    public void testCreatorCallbackOnMessagingRunning() throws IllegalArgumentException {
        ArgumentCaptor<XiMessagingStateListener> deviceControlStateListenerCaptor =
                ArgumentCaptor.forClass(XiMessagingStateListener.class);

        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        XiServiceCreatorCallback<XiMessaging> mockServiceCreatorCallback =
                Mockito.mock(XiServiceCreatorCallback.class);
        messagingCreator.addServiceCreatorCallback(mockServiceCreatorCallback);

        messagingCreator.createMessaging();
        verify(mockMessaging, times(1)).addStateListener(deviceControlStateListenerCaptor.capture());

        deviceControlStateListenerCaptor.getValue().onStateChanged(XiMessaging.State.Running);
        verify(mockServiceCreatorCallback, times(1)).onServiceCreated(eq(mockMessaging));
        verify(mockMessaging, times(1))
                .removeStateListener(eq(deviceControlStateListenerCaptor.getValue()));
    }

    public void testCreatorCallbackOnMessagingError() throws IllegalArgumentException {
        ArgumentCaptor<XiMessagingStateListener> deviceControlStateListenerCaptor =
                ArgumentCaptor.forClass(XiMessagingStateListener.class);

        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        XiServiceCreatorCallback<XiMessaging> mockServiceCreatorCallback =
                Mockito.mock(XiServiceCreatorCallback.class);
        messagingCreator.addServiceCreatorCallback(mockServiceCreatorCallback);

        messagingCreator.createMessaging();
        verify(mockMessaging, times(1)).addStateListener(deviceControlStateListenerCaptor.capture());

        deviceControlStateListenerCaptor.getValue().onError();
        verify(mockServiceCreatorCallback, times(1)).onServiceCreateFailed();
        verify(mockMessaging, times(1))
                .removeStateListener(eq(deviceControlStateListenerCaptor.getValue()));
    }

    public void testCreatorIdleOnMessagingStateChanges() throws IllegalArgumentException {
        ArgumentCaptor<XiMessagingStateListener> deviceControlStateListenerCaptor =
                ArgumentCaptor.forClass(XiMessagingStateListener.class);

        XiMessagingCreatorImpl messagingCreator = new XiMessagingCreatorImpl(mockConnectionPool);
        XiServiceCreatorCallback<XiMessaging> mockServiceCreatorCallback =
                Mockito.mock(XiServiceCreatorCallback.class);
        messagingCreator.addServiceCreatorCallback(mockServiceCreatorCallback);

        messagingCreator.createMessaging();
        verify(mockMessaging, times(1)).addStateListener(deviceControlStateListenerCaptor.capture());

        deviceControlStateListenerCaptor.getValue().onStateChanged(XiMessaging.State.Reconnecting);
        deviceControlStateListenerCaptor.getValue().onStateChanged(XiMessaging.State.Error);
        deviceControlStateListenerCaptor.getValue().onStateChanged(XiMessaging.State.Closed);

        verify(mockServiceCreatorCallback, never()).onServiceCreated(Matchers.<XiMessaging>anyObject());
        verify(mockServiceCreatorCallback, never()).onServiceCreateFailed();
    }
}
