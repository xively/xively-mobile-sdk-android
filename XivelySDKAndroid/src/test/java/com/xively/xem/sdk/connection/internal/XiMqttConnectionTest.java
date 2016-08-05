package com.xively.xem.sdk.connection.internal;

import android.content.Context;

import com.xively.XiSdkConfig;
import com.xively.internal.connection.impl.XiMqttConnectionImpl;
import com.xively.internal.Config;
import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.connection.ConnectionListener;
import com.xively.internal.connection.PublishListener;
import com.xively.internal.device.DeviceInfo;
import com.xively.internal.logger.LMILog;
import com.xively.internal.util.AsyncTimerTask;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;

import junit.framework.TestCase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class XiMqttConnectionTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;

    @Mock
    private MqttAndroidClient mockMqttClient;

    @Mock
    private IMqttDeliveryToken mockMqttDeliveryToken;

    @Mock
    private Context mockContext;

    @Mock
    AsyncTimerTask mockAsyncTimerTask;

    private final String mockUid = "mock uid";
    private final String mockTopic1 = "persistent topic";
    private final int mockMqttMessageId = 2015;

    private XivelyAccount mockXivelyAccount = new XivelyAccount(mockUid, "a username", "some password");

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);

        doNothing().when(mockAsyncTimerTask).execute();
        doNothing().when(mockAsyncTimerTask).cancel();

        when(mockMqttDeliveryToken.getMessageId()).thenReturn(mockMqttMessageId);
        when(mockMqttClient.publish(anyString(), (byte[]) anyObject(), anyInt(), anyBoolean()))
                .thenReturn(mockMqttDeliveryToken);
        when(mockDependencyInjector.createAsyncTimerTask(anyLong(), (Runnable) anyObject()))
                .thenReturn(mockAsyncTimerTask);

        DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
        when(mockDeviceInfo.getUUId()).thenReturn(mockUid);
        when(mockDependencyInjector.deviceInfo()).thenReturn(mockDeviceInfo);
        when(mockDependencyInjector
                .createMqttAndroidClient(Matchers.<Context>anyObject(), anyString(), anyString()))
                .thenReturn(mockMqttClient);
        when(mockDependencyInjector.getContext()).thenReturn(mockContext);
        DependencyInjector.setInstance(mockDependencyInjector);

        Config.resetConfig();
    }


    public void testConnectMqtt() throws Exception {
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        assertEquals(testObject.getClientId(), mockXivelyAccount.getClientId());

        String expectedUri;
        if (Config.CONN_USE_SSL) {
            expectedUri = "ssl://";
        } else {
            expectedUri = "tcp://";
        }
        expectedUri += Config.xi_mqtt_host() + ":";
        expectedUri += Config.CONN_USE_SSL ?
                        Config.CONN_XI_MQTT_SECURE_PORT : Config.CONN_XI_MQTT_PORT;
        verify(mockDependencyInjector).createMqttAndroidClient(any(Context.class),
                eq(expectedUri), eq(mockXivelyAccount.getUserName()));

        verify(mockMqttClient).registerResources(any(Context.class));
        verify(mockMqttClient).connect(argThat(new BaseMatcher<MqttConnectOptions>() {
                    public boolean matches(Object options) {
                        return ((MqttConnectOptions) options).isCleanSession();
                    }

                    public void describeTo(Description var1) {
                    }
                })

                , any(Context.class),
                any(IMqttActionListener.class));
    }

    public void testConnectMqttUncleanSession() throws Exception {
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", false, null);

        assertEquals(testObject.getClientId(), mockXivelyAccount.getClientId());

        String expectedUri;
        if (Config.CONN_USE_SSL) {
            expectedUri = "ssl://";
        } else {
            expectedUri = "tcp://";
        }
        expectedUri += Config.xi_mqtt_host() + ":";
        expectedUri += Config.CONN_USE_SSL ?
                Config.CONN_XI_MQTT_SECURE_PORT : Config.CONN_XI_MQTT_PORT;
        verify(mockDependencyInjector).createMqttAndroidClient(any(Context.class),
                eq(expectedUri), eq(mockXivelyAccount.getUserName()));

        verify(mockMqttClient).registerResources(any(Context.class));
        verify(mockMqttClient).connect(argThat(new BaseMatcher<MqttConnectOptions>() {
            public boolean matches(Object options) {
                return !((MqttConnectOptions) options).isCleanSession();
            } public void describeTo(Description var1) {}})

                , any(Context.class),
                any(IMqttActionListener.class));
    }

    public void testConnectMqttLastWill() throws Exception {
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        XiLastWill lastWill = new XiLastWill("aaaaaa", "bbbb".getBytes(),
                XiMessaging.XiMessagingQoS.AtLeastOnce, false);
        testObject.connect(mockXivelyAccount, "jwt", false, lastWill);

        assertEquals(testObject.getClientId(), mockXivelyAccount.getClientId());

        String expectedUri;
        if (Config.CONN_USE_SSL) {
            expectedUri = "ssl://";
        } else {
            expectedUri = "tcp://";
        }
        expectedUri += Config.xi_mqtt_host() + ":";
        expectedUri += Config.CONN_USE_SSL ?
                Config.CONN_XI_MQTT_SECURE_PORT : Config.CONN_XI_MQTT_PORT;
        verify(mockDependencyInjector).createMqttAndroidClient(any(Context.class),
                eq(expectedUri), eq(mockXivelyAccount.getUserName()));

        verify(mockMqttClient).registerResources(any(Context.class));
        verify(mockMqttClient).connect(argThat(new BaseMatcher<MqttConnectOptions>() {
                    public boolean matches(Object options) {
                        MqttConnectOptions o = (MqttConnectOptions)options;
                        return o.getWillDestination().equals("aaaaaa") &&
                                Arrays.equals(o.getWillMessage().getPayload(), "bbbb".getBytes()) &&
                                o.getWillMessage().getQos() == 1 &&
                                !o.getWillMessage().isRetained();
                    } public void describeTo(Description var1) {}})

                , any(Context.class),
                any(IMqttActionListener.class));
    }


    public void testConnectMqttCallsOnErrorOnFailureCallback() throws Exception {
        Config.CONN_MQTT_MAX_RECONNECT = 0;

        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        ArgumentCaptor<IMqttActionListener> mqttActionCaptor = ArgumentCaptor.forClass(IMqttActionListener.class);
        verify(mockMqttClient).connect(any(MqttConnectOptions.class), any(Context.class),
                mqttActionCaptor.capture());
        mqttActionCaptor.getValue().onFailure(null, new RuntimeException());

        verify(mockConnectionListener).onError(eq(ConnectionListener.ConnectionError.FAILED_TO_CONNECT));

        Config.resetConfig();
    }

    
    public void testConnectMqttCallsOnErrorOnMqttException() throws Exception {
        Config.CONN_MQTT_MAX_RECONNECT = 0;

        doThrow(new MqttException(1)).
            when(mockMqttClient).connect(any(MqttConnectOptions.class), any(Context.class),
                any(IMqttActionListener.class));

        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        verify(mockConnectionListener).onError(eq(ConnectionListener.ConnectionError.FAILED_TO_CONNECT));

        Config.resetConfig();
    }


    
    public void testConnectMqttReconnectsOnError() throws Exception {
        Config.CONN_MQTT_MAX_RECONNECT = 10;
        Config.CONN_MQTT_RECONNECT_DELAY = 0;

        when(mockMqttClient.connect(any(MqttConnectOptions.class), any(Context.class),
                Matchers.<IMqttActionListener>anyObject())).thenThrow(new MqttException(1));
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);

        testObject.connect(mockXivelyAccount, "jwt", true, null);

        verify(mockMqttClient, times(Config.CONN_MQTT_MAX_RECONNECT + 1))
                .connect(any(MqttConnectOptions.class), any(Context.class),
                        Matchers.<IMqttActionListener>anyObject());
        verify(mockConnectionListener).onError(eq(ConnectionListener.ConnectionError.FAILED_TO_CONNECT));

        Config.resetConfig();
    }

    
    public void testConnectMqttOptionsValid() throws Exception {
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        verify(mockMqttClient, times(1)).registerResources(any(Context.class));
        ArgumentCaptor<MqttConnectOptions> mqttOptionsCaptor =
                ArgumentCaptor.forClass(MqttConnectOptions.class);
        verify(mockMqttClient, times(1)).connect(mqttOptionsCaptor.capture(), any(Context.class),
                any(IMqttActionListener.class));

        MqttConnectOptions options = mqttOptionsCaptor.getValue();
        assertNotNull(options);
        assertEquals(options.getUserName(), "Auth:JWT");
        assertTrue(Arrays.equals(options.getPassword(), "jwt".toCharArray()));
        assertTrue(options.isCleanSession());
        assertEquals(options.getConnectionTimeout(), Config.CONN_MQTT_MAX_TIMEOUT);
        assertEquals(options.getKeepAliveInterval(), Config.CONN_KEEPALIVE);
    }

    public void testConnectTimeoutSetsOnError(){
        ArgumentCaptor<Runnable> timerRunnable = ArgumentCaptor.forClass(Runnable.class);
        when(mockMqttClient.isConnected()).thenReturn(false);

        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);

        testObject.connect(mockXivelyAccount, "jwt", true, null);

        verify(mockDependencyInjector).createAsyncTimerTask(anyLong(), timerRunnable.capture());
        assertNotNull(timerRunnable.getValue());
        //run timeout code
        timerRunnable.getValue().run();

        verify(mockConnectionListener).onError(ConnectionListener.ConnectionError.TIMED_OUT);
    }
    
    public void testIsConnected() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        assertTrue(testObject.isConnected());
    }

    
    public void testIsConnectedFalse() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(false);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        assertFalse(testObject.isConnected());
    }

    
    public void testGetClientId() throws Exception {
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        assertEquals(testObject.getClientId(), mockUid);
    }

    
    public void testDisconnectWithSuccessClientDisconnectCallback() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);
        assertTrue(testObject.isConnected());

        testObject.disconnect();

        ArgumentCaptor<IMqttActionListener> mqttActionListenerCaptor =
                ArgumentCaptor.forClass(IMqttActionListener.class);

        verify(mockMqttClient).setCallback((org.eclipse.paho.client.mqttv3.MqttCallback) isNull());
        verify(mockConnectionListener).onDisconnected();
        verify(mockMqttClient).disconnect(eq((long) Config.CONN_MQTT_DISCONNECT_TIMEOUT),
                eq(mockContext), mqttActionListenerCaptor.capture());

        mqttActionListenerCaptor.getValue().onSuccess(null);

        verify(mockMqttClient).unregisterResources();
        verify(mockConnectionListener, atLeastOnce()).onDisconnected();
    }

    
    public void testDisconnectWithFailureClientDisconnectCallback() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);
        assertTrue(testObject.isConnected());

        testObject.disconnect();

        ArgumentCaptor<IMqttActionListener> mqttActionListenerCaptor =
                ArgumentCaptor.forClass(IMqttActionListener.class);

        verify(mockMqttClient).setCallback((org.eclipse.paho.client.mqttv3.MqttCallback) isNull());
        verify(mockConnectionListener, atLeastOnce()).onDisconnected();
        verify(mockMqttClient).disconnect(eq((long) Config.CONN_MQTT_DISCONNECT_TIMEOUT),
                eq(mockContext), mqttActionListenerCaptor.capture());

        mqttActionListenerCaptor.getValue().onFailure(null, null);

        verify(mockMqttClient).unregisterResources();
    }

    
    public void testDisconnectWithClientNotConnected() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(false);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        testObject.disconnect();
        verify(mockConnectionListener).onDisconnected();
    }

    
    public void testDisconnectCallsOnErrorIfMqttExceptionThrown() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        when(mockMqttClient.disconnect(anyLong(), any(Context.class), any(IMqttActionListener.class)))
                .thenThrow(new MqttException(MqttException.REASON_CODE_CLIENT_EXCEPTION));

        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        testObject.disconnect();

        verify(mockConnectionListener).onError(ConnectionListener.ConnectionError.DISCONNECT_ERROR);
    }

    
    public void testPublish() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        String testMessage = "some test message";
        testObject.publish(testMessage, mockTopic1);
        verify(mockMqttClient).publish(eq(mockTopic1), eq(testMessage.getBytes()),
                eq(Config.CONN_XI_MQTT_QOS), eq(false));
    }

    
    public void testPublishNoInteractionOnPublishException() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        when(mockMqttClient.publish(anyString(), (byte[]) any(), anyInt(), anyBoolean()))
                .thenThrow(new MqttException(MqttException.REASON_CODE_INVALID_MESSAGE));
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        String testMessage = "some test message";
        testObject.publish(testMessage, mockTopic1);
        verify(mockMqttClient).publish( eq(mockTopic1), eq(testMessage.getBytes()),
                eq(Config.CONN_XI_MQTT_QOS), eq(false) );
    }

    
    public void testMessageArrived() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        PublishListener mockPublishListener = mock(PublishListener.class);
        testObject.addPublishListener(mockPublishListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        String testString = "some test message";
        MqttMessage testMessage = new MqttMessage(testString.getBytes());
        ArgumentCaptor<MqttCallback> mqttCallbackCaptor = ArgumentCaptor.forClass(MqttCallback.class);
        verify(mockMqttClient).setCallback(mqttCallbackCaptor.capture());

        mqttCallbackCaptor.getValue().messageArrived(mockTopic1, testMessage);
        verify(mockPublishListener).onPublishReceived(eq(testString), eq(mockTopic1));
    }

    public void testPublishReturnsExpectedId(){
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        String testMessage = "some test message";
        assertEquals(mockMqttMessageId, testObject.publish(testMessage, mockTopic1));
    }
    
    public void testExpectDisconnectedAndErrorCallbackOnConnectionLost() throws Exception {
        Config.CONN_MQTT_MAX_RECONNECT = 0;

        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.connect(mockXivelyAccount, "jwt", true, null);

        ArgumentCaptor<MqttCallback> mqttCallbackCaptor = ArgumentCaptor.forClass(MqttCallback.class);
        verify(mockMqttClient).setCallback(mqttCallbackCaptor.capture());

        mqttCallbackCaptor.getValue().connectionLost(null);
        verify(mockConnectionListener).onDisconnected();
        verify(mockConnectionListener).onError(ConnectionListener.ConnectionError.CONNECTION_LOST);

        Config.resetConfig();
    }

    
    public void testRemoveConnectionListener() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        PublishListener mockPublishListener = mock(PublishListener.class);
        testObject.addPublishListener(mockPublishListener);
        testObject.removePublishListener(mockPublishListener);

        testObject.connect(mockXivelyAccount, "jwt", true, null);

        testObject.publish("dummy", mockTopic1);
        testObject.disconnect();

        verifyNoMoreInteractions(mockPublishListener);
    }

    
    public void testRemovePublishListener() throws Exception {
        when(mockMqttClient.isConnected()).thenReturn(true);
        XiMqttConnectionImpl testObject = new XiMqttConnectionImpl();
        ConnectionListener mockConnectionListener = mock(ConnectionListener.class);
        testObject.addConnectionListener(mockConnectionListener);
        testObject.removeConnectionListener(mockConnectionListener);

        testObject.connect(mockXivelyAccount, "jwt", true, null);

        ArgumentCaptor<MqttCallback> mqttCallbackCaptor = ArgumentCaptor.forClass(MqttCallback.class);
        verify(mockMqttClient).setCallback(mqttCallbackCaptor.capture());

        mqttCallbackCaptor.getValue().messageArrived("mock topic", new MqttMessage());

        testObject.publish("dummy", mockTopic1);
        testObject.disconnect();

        verifyNoMoreInteractions(mockConnectionListener);
    }
}