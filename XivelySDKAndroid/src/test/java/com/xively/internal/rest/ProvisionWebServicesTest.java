package com.xively.internal.rest;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.provision.StartAssociationWithCode;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisionWebServicesTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testStartAssociationWithCode() throws Exception {
        ProvisionWebServices testWS = new ProvisionWebServices(mockRestAdapter);

        Callback<StartAssociationWithCode.Response> mockCallback = mock(Callback.class);
        StartAssociationWithCode mockAssociation = mock(StartAssociationWithCode.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockAssociation);

        String userId = "mock user Id";
        String code = "mock association code";

        ArgumentCaptor associationReqCaptor = ArgumentCaptor.forClass(StartAssociationWithCode.Request.class);


        testWS.associateIoTDevice(code, userId, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<StartAssociationWithCode>>anyObject());

        verify(mockAssociation, timeout(1000).times(1)).
                startAssociationWithCode(
                        (StartAssociationWithCode.Request) associationReqCaptor.capture(),
                        eq(mockCallback));

        assertNotNull(associationReqCaptor.getValue());
        assertTrue(((StartAssociationWithCode.Request) associationReqCaptor.getValue())
                .endUserId.equals(userId));
        assertTrue(((StartAssociationWithCode.Request)associationReqCaptor.getValue())
                .associationCode.equals(code));
    }

}