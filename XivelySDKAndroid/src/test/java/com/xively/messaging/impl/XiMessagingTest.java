package com.xively.messaging.impl;

import com.xively.XiException;
import com.xively.XiSdkConfig;
import com.xively.internal.connection.ConnectionListener;
import com.xively.internal.connection.PublishListener;
import com.xively.internal.connection.XiMqttConnection;
import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.messaging.XiMessagingImpl;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingDataListener;
import com.xively.messaging.XiMessagingStateListener;
import com.xively.messaging.XiMessagingSubscriptionListener;

import junit.framework.TestCase;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class XiMessagingTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private XiMqttConnection connectedConnection;
    @Mock
    private XiMqttConnection unconnectedConnection;
    @Mock
    private XiMqttConnectionPool mockConnectionPool;
    @Mock
    private XiMessagingStateListener mockStateListener;
    @Mock
    private XiMessagingDataListener mockDataListener;
    @Mock
    private XiMessagingSubscriptionListener mockSubscriptionListener;

    private final String testTopic1 = "test topic 1";
    private final String testTopic2 = "test topic 2";

    XiLastWill lastWill = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);

        MockitoAnnotations.initMocks(this);

        when(connectedConnection.isConnected()).thenReturn(true);
        when(unconnectedConnection.isConnected()).thenReturn(false);

        when(mockDependencyInjector.createMqttConnectionPool(Matchers.<XivelyAccount>anyObject()))
                .thenReturn(mockConnectionPool);
        DependencyInjector.setInstance(mockDependencyInjector);
        //XiSessionImpl.get().setMqttConnectionPool(mockConnectionPool);

        this.lastWill = new XiLastWill("aaa", "bbb".getBytes(),
                XiMessaging.XiMessagingQoS.AtLeastOnce, false);
    }

    public void testMessagingDefaultState(){
        XiMessaging deviceControl = new XiMessagingImpl(mockConnectionPool,"jwt");

        assertEquals(XiMessaging.State.Closed, deviceControl.getState());
    }

    public void testMessagingInitOnConnectionReady() throws IllegalArgumentException {
        ArgumentCaptor<XiMqttConnectionPool.MqttConnectionRequestCallback> connRequestCaptor =
                ArgumentCaptor.forClass(XiMqttConnectionPool.MqttConnectionRequestCallback.class);

        XiMessagingImpl deviceControl = new XiMessagingImpl(mockConnectionPool,"jwt");
        deviceControl.addStateListener(mockStateListener);
        deviceControl.init(false, this.lastWill);

        verify(mockConnectionPool, times(1)).requestConnection(eq(deviceControl), eq("jwt"), eq(false),
                eq(this.lastWill), connRequestCaptor.capture());
        connRequestCaptor.getValue().onConnectionReady(connectedConnection);

        verify(connectedConnection).addConnectionListener(eq(deviceControl));
        verify(connectedConnection).addPublishListener(eq(deviceControl));
        verify(mockStateListener).onStateChanged(XiMessaging.State.Running);
    }

    public void testMessagingInitOnConnectFailed() throws IllegalArgumentException {
        ArgumentCaptor<XiMqttConnectionPool.MqttConnectionRequestCallback> connRequestCaptor =
                ArgumentCaptor.forClass(XiMqttConnectionPool.MqttConnectionRequestCallback.class);

        XiMessagingImpl deviceControl = new XiMessagingImpl(mockConnectionPool,"jwt");
        deviceControl.addStateListener(mockStateListener);
        deviceControl.init(false, this.lastWill);

        verify(mockConnectionPool, times(1)).requestConnection(eq(deviceControl), eq("jwt"), eq(false),
                eq(this.lastWill), connRequestCaptor.capture());

        connRequestCaptor.getValue().onConnectFailed(ConnectionListener.ConnectionError.FAILED_TO_CONNECT);
        verify(mockStateListener).onStateChanged(XiMessaging.State.Error);
    }

    public void testMessagingClose() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addStateListener(mockStateListener);
        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
            deviceControl.subscribe(testTopic2, XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (XiException e) {
            fail(e.toString());
        }

        deviceControl.close();

        verify(connectedConnection).removePublishListener(deviceControl);
        verify(connectedConnection).removePublishListener(deviceControl);
        verify(mockStateListener).onStateChanged(XiMessaging.State.Closed);
    }


    public void testMessagingCloseWithUnsubscribeException() throws MqttException, IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addStateListener(mockStateListener);
        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
            deviceControl.subscribe(testTopic2, XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (XiException e) {
            fail(e.toString());
        }

        doThrow(new MqttException(0)).when(connectedConnection).unsubscribeFromTopic(anyString());

        deviceControl.close();

        verify(connectedConnection).removePublishListener(deviceControl);
        verify(connectedConnection).removePublishListener(deviceControl);
        verify(mockStateListener).onStateChanged(XiMessaging.State.Closed);
    }

    public void testMessagingAddNullDataListener() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);

        try {
            deviceControl.addDataListener(null);
        } catch (Exception ex){
            fail(ex.toString());
        }

        //nothing should happen
    }

    public void testMessagingMultipleAddDataListener() throws IllegalArgumentException {
        ArgumentCaptor<PublishListener> publishListenerCaptor =
                ArgumentCaptor.forClass(PublishListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        verify(connectedConnection).addPublishListener(publishListenerCaptor.capture());
        deviceControl.addDataListener(mockDataListener);
        deviceControl.addDataListener(mockDataListener);
        deviceControl.addDataListener(mockDataListener);

        assertNotNull(publishListenerCaptor.getValue());
        publishListenerCaptor.getValue().onPublishReceived("test message", testTopic2);
        verify(mockDataListener).onDataReceived(AdditionalMatchers.aryEq("test message".getBytes()), eq(testTopic2));
    }


    public void testMessagingRemoveDataListener() throws IllegalArgumentException {
        ArgumentCaptor<PublishListener> publishListenerCaptor =
                ArgumentCaptor.forClass(PublishListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        verify(connectedConnection).addPublishListener(publishListenerCaptor.capture());
        deviceControl.addDataListener(mockDataListener);

        assertNotNull(publishListenerCaptor.getValue());
        publishListenerCaptor.getValue().onPublishReceived("test message", testTopic2);
        verify(mockDataListener).onDataReceived(AdditionalMatchers.aryEq("test message".getBytes()), eq(testTopic2));

        deviceControl.removeDataListener(mockDataListener);
        publishListenerCaptor.getValue().onPublishReceived("test message 2", testTopic2);
        verify(mockDataListener, never()).onDataReceived(AdditionalMatchers.aryEq("test message 2".getBytes()), eq(testTopic2));
    }

    public void testMessagingRemoveNullDataListener() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);

        try {
            deviceControl.removeDataListener(null);
        } catch (Exception ex){
            fail(ex.toString());
        }

        //nothing should happen
    }

    public void testMessagingStateOnConnStateChanges() throws IllegalArgumentException {
        ArgumentCaptor<ConnectionListener> connListenerCaptor =
                ArgumentCaptor.forClass(ConnectionListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addStateListener(mockStateListener);
        //test add listener multiple times
        deviceControl.addStateListener(mockStateListener);
        deviceControl.addStateListener(mockStateListener);
        deviceControl.addStateListener(mockStateListener);

        verify(connectedConnection).addConnectionListener(connListenerCaptor.capture());

        connListenerCaptor.getValue().onReconnecting();
        verify(mockStateListener).onStateChanged(XiMessaging.State.Reconnecting);
        Mockito.reset(mockStateListener);

        connListenerCaptor.getValue().onReconnected();
        verify(mockStateListener).onStateChanged(XiMessaging.State.Running);
        Mockito.reset(mockStateListener);

        connListenerCaptor.getValue().onDisconnected();
        verify(mockStateListener).onStateChanged(XiMessaging.State.Closed);
        Mockito.reset(mockStateListener);

        connListenerCaptor.getValue().onConnected();
        verify(mockStateListener).onStateChanged(XiMessaging.State.Running);
        Mockito.reset(mockStateListener);

        connListenerCaptor.getValue().onClosed();
        verify(mockStateListener).onStateChanged(XiMessaging.State.Closed);
        Mockito.reset(mockStateListener);

        connListenerCaptor.getValue().onError(ConnectionListener.ConnectionError.CONNECTION_LOST);
        verify(mockStateListener).onStateChanged(XiMessaging.State.Error);
        verify(mockStateListener).onError();
        Mockito.reset(mockStateListener);
    }

    public void testMessagingStateChangeOnSameStateEvent() throws IllegalArgumentException {
        ArgumentCaptor<ConnectionListener> connListenerCaptor =
                ArgumentCaptor.forClass(ConnectionListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addStateListener(mockStateListener);

        verify(connectedConnection).addConnectionListener(connListenerCaptor.capture());

        connListenerCaptor.getValue().onReconnecting();
        connListenerCaptor.getValue().onReconnecting();
        connListenerCaptor.getValue().onReconnecting();
        connListenerCaptor.getValue().onReconnecting();
        verify(mockStateListener, times(1)).onStateChanged(XiMessaging.State.Reconnecting);
    }

    public void testMessagingAddNullStateListener() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        try {
            deviceControl.addStateListener(null);
        } catch (Exception ex){
            fail(ex.toString());
        }

        //nothing should happen
    }

    public void testMessagingStateListenerRemove() throws IllegalArgumentException {
        ArgumentCaptor<ConnectionListener> connListenerCaptor =
                ArgumentCaptor.forClass(ConnectionListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addStateListener(mockStateListener);
        deviceControl.removeStateListener(mockStateListener);

        verify(connectedConnection).addConnectionListener(connListenerCaptor.capture());

        connListenerCaptor.getValue().onReconnecting();
        connListenerCaptor.getValue().onConnected();
        connListenerCaptor.getValue().onDisconnected();

        verify(mockStateListener, never()).onError();
        verify(mockStateListener, never()).onStateChanged(Matchers.<XiMessaging.State>anyObject());
    }

    public void testMessagingRemoveNullStateListener() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);

        try {
            deviceControl.removeStateListener(null);
        } catch (Exception ex){
            fail(ex.toString());
        }

        //nothing should happen
    }

    public void testMessagingSubscribe() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
            deviceControl.removeSubscriptionListener(mockSubscriptionListener);
            deviceControl.subscribe(testTopic2, XiMessaging.XiMessagingQoS.AtMostOnce);
        } catch (XiException e) {
            fail(e.toString());
        }

        verify(mockSubscriptionListener, times(1)).onSubscribed(eq(testTopic1));
        verify(mockSubscriptionListener,never()).onSubscribed(eq(testTopic2));

        try {
            verify(connectedConnection).subscribeToTopic(eq(testTopic1), eq(0));
        } catch (MqttException e) {
            fail(e.toString());
        }
    }

    public void testMessagingSubscribeThrowsExceptionOnNotConnected() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(unconnectedConnection);
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.ExactlyOnce);
        } catch (XiException e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingSubscribeThrowsExceptionOnNullConnection() {
        XiMessagingImpl deviceControl = new XiMessagingImpl(null,"jwt");

        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (XiException e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingUnsubscribe() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
            deviceControl.unsubscribe(testTopic1);
            deviceControl.unsubscribe(testTopic2);
        } catch (XiException e) {
            fail(e.toString());
        }

        verify(mockSubscriptionListener, times(1)).onUnsubscribed(eq(testTopic1));
        verify(mockSubscriptionListener, times(1)).onUnsubscribed(eq(testTopic2));
    }

    public void testMessagingSubscribeThrowsExceptionOnMqttError() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            doThrow(new MqttException(0))
                    .when(connectedConnection).subscribeToTopic(anyString(), anyInt());
        } catch (MqttException ex){
            fail(ex.toString());
        }

        try {
            deviceControl.subscribe(testTopic1, XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (XiException e) {
            assertTrue(e instanceof XiException.ConnectionException);
        }
    }

    public void testMessagingUnsubscribeThrowsExceptionOnNotConnected() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(unconnectedConnection);
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            deviceControl.unsubscribe(testTopic1);
        } catch (XiException e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }

        verify(mockSubscriptionListener).onUnsubscribeFailed(eq(testTopic1));
    }

    public void testMessagingUnsubscribeThrowsExceptionOnNullConnection() {
        XiMessagingImpl deviceControl = new XiMessagingImpl(null,"jwt");
        deviceControl.addSubscriptionListener(mockSubscriptionListener);

        try {
            deviceControl.unsubscribe(testTopic1);
        } catch (XiException e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }

        verify(mockSubscriptionListener).onUnsubscribeFailed(eq(testTopic1));
    }

    public void testMessagingPublish() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addDataListener(mockDataListener);

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (XiException.NotConnectedException e) {
            fail(e.toString());
        }

        verify(connectedConnection).publish(AdditionalMatchers.aryEq("test message".getBytes()),
                eq(testTopic1), eq(0), eq(false));

    }

    public void testMessagingPublishRetained() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        deviceControl.addDataListener(mockDataListener);

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(),
                    XiMessaging.XiMessagingQoS.AtLeastOnce, true);
        } catch (XiException.NotConnectedException e) {
            fail(e.toString());
        }

        verify(connectedConnection).publish(AdditionalMatchers.aryEq("test message".getBytes()),
                eq(testTopic1), eq(0), eq(true));
    }

    public void testMessagingPublishThrowsExceptionOnNotConnected() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(unconnectedConnection);
        deviceControl.addDataListener(mockDataListener);

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (Exception e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingPublishThrowsExceptionOnNullConnection(){
        XiMessagingImpl deviceControl = new XiMessagingImpl(null,"jwt");

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(),
                    XiMessaging.XiMessagingQoS.AtLeastOnce);
        } catch (Exception e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingPublishRetainedThrowsExceptionOnNullConnection(){
        XiMessagingImpl deviceControl = new XiMessagingImpl(null,"jwt");

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(),
                    XiMessaging.XiMessagingQoS.AtLeastOnce, true);
        } catch (Exception e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingPublishRetainedThrowsExceptionOnNotConnected() throws IllegalArgumentException {
        XiMessagingImpl deviceControl = setupNewTestObject(unconnectedConnection);
        deviceControl.addDataListener(mockDataListener);

        try {
            deviceControl.publish(testTopic1, "test message".getBytes(),
                    XiMessaging.XiMessagingQoS.AtLeastOnce, true);
        } catch (Exception e) {
            assertTrue(e instanceof XiException.NotConnectedException);
        }
    }

    public void testMessagingDataSentCallback() throws IllegalArgumentException {
        ArgumentCaptor<PublishListener> publishListenerCaptor =
                ArgumentCaptor.forClass(PublishListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        verify(connectedConnection).addPublishListener(publishListenerCaptor.capture());
        deviceControl.addDataListener(mockDataListener);

        assertNotNull(publishListenerCaptor.getValue());
        publishListenerCaptor.getValue().onMessageDeliveryConfirmation(123);

        verify(mockDataListener).onDataSent(eq(123));
    }

    public void testMessagingReceiveData() throws IllegalArgumentException {
        ArgumentCaptor<PublishListener> publishListenerCaptor =
                ArgumentCaptor.forClass(PublishListener.class);

        XiMessagingImpl deviceControl = setupNewTestObject(connectedConnection);
        verify(connectedConnection).addPublishListener(publishListenerCaptor.capture());
        deviceControl.addDataListener(mockDataListener);

        assertNotNull(publishListenerCaptor.getValue());
        publishListenerCaptor.getValue().onPublishReceived("test message", testTopic2);

        verify(mockDataListener).onDataReceived(AdditionalMatchers.aryEq("test message".getBytes()), eq(testTopic2));
    }


    private XiMessagingImpl setupNewTestObject(XiMqttConnection mqttConnection) throws IllegalArgumentException {
        ArgumentCaptor<XiMqttConnectionPool.MqttConnectionRequestCallback> connRequestCaptor =
                ArgumentCaptor.forClass(XiMqttConnectionPool.MqttConnectionRequestCallback.class);

        XiMessagingImpl deviceControl = new XiMessagingImpl(mockConnectionPool,"jwt");

        deviceControl.init(false, null);

        verify(mockConnectionPool, times(1)).requestConnection(eq(deviceControl), eq("jwt"), eq(false), eq((XiLastWill) null),
                connRequestCaptor.capture());
        connRequestCaptor.getValue().onConnectionReady(mqttConnection);
        return deviceControl;
    }

}
