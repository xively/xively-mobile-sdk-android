package com.xively.internal.connection.impl;

import com.xively.XiSdkConfig;
import com.xively.XiService;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.connection.XiMqttConnection;
import com.xively.internal.logger.LMILog;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XiMqttConnectionPoolTest extends TestCase {

    @Mock
    private XivelyAccount mockAccount;
    @Mock
    private XiMqttConnection mockConn;
    @Mock
    private XiMqttConnectionPool.MqttConnectionRequestCallback mockRequestCallback;
    @Mock
    private XiService mockXiService;
    @Mock
    private XiService mockXiService2;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);

    }

    public void testGetConnectionWithConnectionReady(){
        when(mockConn.isConnected()).thenReturn(true);
        when(mockConn.isCleanSession()).thenReturn(true);
        when(mockConn.getLastWill()).thenReturn(null);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);
        try {
            connPool.requestConnection(mockXiService2, "jwt", true, null, mockRequestCallback);
        } catch (Exception e) {}
        verify(mockRequestCallback).onConnectionReady(eq(mockConn));
    }

    public void testGetConnectionWithConnectionReadyButDifferentCleanSession(){
        when(mockConn.isConnected()).thenReturn(true);
        when(mockConn.isCleanSession()).thenReturn(true);
        when(mockConn.getLastWill()).thenReturn(null);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);

        try {
            connPool.requestConnection(mockXiService, "jwt", false, null, mockRequestCallback);
            fail();
        } catch (Exception e) {}

    }

    public void testGetConnectionWithConnectionReadyButWithLastWill(){
        when(mockConn.isConnected()).thenReturn(true);
        when(mockConn.isCleanSession()).thenReturn(true);
        when(mockConn.getLastWill()).thenReturn(null);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);
        XiLastWill lastWill = new XiLastWill("topic", "aa".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce, false);
        try {
            connPool.requestConnection(mockXiService, "jwt", true, lastWill, mockRequestCallback);
            fail();
        } catch (Exception e) {}
    }

    public void testGetConnectionWithConnectionReadyButWithDifferentLastWill(){
        when(mockConn.isConnected()).thenReturn(true);
        when(mockConn.isCleanSession()).thenReturn(true);
        XiLastWill firstLastWill =
                new XiLastWill("topic", "aa".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce, false);
        when(mockConn.getLastWill()).thenReturn(firstLastWill);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);

        XiLastWill secondLastWill =
                new XiLastWill("topic", "bb".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce, false);
        try {
            connPool.requestConnection(mockXiService, "jwt", true, secondLastWill, mockRequestCallback);
            fail();
        } catch (Exception e) {}
    }

    public void testReleaseLastServiceClosesConnection(){
        when(mockConn.isConnected()).thenReturn(true);
        when(mockConn.isCleanSession()).thenReturn(true);
        when(mockConn.getLastWill()).thenReturn(null);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);

        try {
            connPool.requestConnection(mockXiService, "jwt", true, null, mockRequestCallback);
            connPool.requestConnection(mockXiService2, "jwt", true, null, mockRequestCallback);
            connPool.releaseConnection(mockXiService2);
            connPool.releaseConnection(mockXiService);
        } catch (Exception e) {}


        verify(mockConn).disconnect();
    }

    public void testReleaseService(){
        when(mockConn.isConnected()).thenReturn(true);

        XiMqttConnectionPool connPool = new XiMqttConnectionPool(mockAccount);
        connPool.setTestConnectionObject(mockConn);

        try {
            connPool.requestConnection(mockXiService2, "jwt", true, null, mockRequestCallback);
            connPool.requestConnection(mockXiService2, "jwt", true, null, mockRequestCallback);
            connPool.releaseConnection(mockXiService);
        } catch (Exception e) {}

        verify(mockConn, never()).disconnect();
    }

    public void testGetConnectionCallbackWithDisconnectedConnection(){
        //TODO: implement
    }

    public void testGetConnectionCallbackOnConnectionError(){
        //TODO: implement
    }

    public void testGetConnectionCallbackOnConnectionClosed(){
        //TODO: implement
    }

}
