package com.xively.internal.rest.blueprint;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.Callback;
import retrofit.RestAdapter;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlueprintGetUsersTest extends TestCase {

    @Mock
    RestAdapter mockRestAdapter;

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);
    }

    public void testStartGetEndUserQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<GetEndUsers.Response> mockCallback = mock(Callback.class);
        GetEndUsers mockGetEndUser = mock(GetEndUsers.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetEndUser);

        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.getEndUsers(accountId, userId, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<GetEndUsers>>anyObject());
        verify(mockGetEndUser, timeout(1000).times(1)).getEndUsers(
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString(),
                eq(mockCallback)
        );

    }

    public void testStartGeAccountUserQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<GetAccountUser.Response> mockCallback = mock(Callback.class);
        GetAccountUser mockGetAccountUser = mock(GetAccountUser.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockGetAccountUser);

        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.getAccountUser(accountId, userId, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<GetAccountUser>>anyObject());
        verify(mockGetAccountUser, timeout(1000).times(1)).getAccountUser(
                eq(accountId),
                eq(userId),
                eq(true),
                eq(true),
                eq(1),
                anyInt(),
                anyString(),
                eq(mockCallback)
        );

    }

    public void testStartCreateCredentialsQuery() throws Exception {
        BlueprintWebServices testWS = new BlueprintWebServices(mockRestAdapter);

        Callback<CreateCredentials.Response> mockCallback = mock(Callback.class);
        CreateCredentials mockCreateCredentials = mock(CreateCredentials.class);
        when(mockRestAdapter.create(Matchers.<Class<Object>>anyObject())).thenReturn(mockCreateCredentials);

        ArgumentCaptor<CreateCredentials.Request> createCredentialsRequestCaptor =
                ArgumentCaptor.forClass(CreateCredentials.Request.class);

        String accountId = "mock account id";
        String userId = "mock access user id";

        testWS.createCredentials(accountId, userId, BlueprintWebServices.BluePrintEntity.endUser, mockCallback);

        verify(mockRestAdapter, times(1)).create(Matchers.<Class<CreateCredentials>>anyObject());
        verify(mockCreateCredentials, timeout(1000).times(1)).createCredentials(
                createCredentialsRequestCaptor.capture(),
                eq(mockCallback)
        );

        CreateCredentials.Request request = createCredentialsRequestCaptor.getValue();
        assertNotNull(request);
        assertEquals(accountId, request.accountId);
        assertEquals(userId, request.entityId);
        assertEquals("endUser", request.entityType);
    }

}