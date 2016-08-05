package com.xively.internal.device;

import android.content.Context;
import android.content.SharedPreferences;

import com.xively.XiSdkConfig;
import com.xively.internal.DependencyInjector;
import com.xively.internal.device.impl.DeviceInfoImpl;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeviceInfoTest extends TestCase {

    @Mock
    private DependencyInjector mockDependencyInjector;
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    private final String mockUId = "mock unique id";

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);

        when(mockDependencyInjector.getContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        DependencyInjector.setInstance(mockDependencyInjector);
    }

    public void testGetUUIdGeneratesAndStoresId(){
        ArgumentCaptor<String> prefIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> prefValueCaptor = ArgumentCaptor.forClass(String.class);

        when(mockSharedPreferences.getString(anyString(), Matchers.<String>anyObject()))
                .thenReturn(null);


        DeviceInfoImpl testDeviceInfo = new DeviceInfoImpl();
        String uuid = testDeviceInfo.getUUId();

        verify(mockSharedPreferences, times(1))
                .getString(prefIdCaptor.capture(), Matchers.<String>anyObject());
        String idKey = prefIdCaptor.getValue();

        verify(mockEditor, times(1)).putString(eq(idKey), prefValueCaptor.capture());
        verify(mockEditor, times(1)).apply();
        assertEquals(prefValueCaptor.getValue(), uuid);
    }

    public void testGetUUIdReturnsStoredValue(){
        when(mockSharedPreferences.getString(anyString(), Matchers.<String>anyObject()))
                .thenReturn(mockUId);

        DeviceInfoImpl testDeviceInfo = new DeviceInfoImpl();
        String uuid = testDeviceInfo.getUUId();

        assertEquals(mockUId, uuid);
        verify(mockEditor, never()).putString(anyString(), anyString());
        verify(mockEditor, never()).apply();
    }

    public void testGetUUIdReturnsCachedValue(){
        DeviceInfoImpl testDeviceInfo = new DeviceInfoImpl();
        String uuid = testDeviceInfo.getUUId();

        for (int i=0; i<10; i++){
            String uuid2 = testDeviceInfo.getUUId();
            assertEquals(uuid, uuid2);
        }

        verify(mockSharedPreferences, times(1)).getString(anyString(), anyString());
        verify(mockEditor, times(1)).putString(anyString(), eq(uuid));
        verify(mockEditor, times(1)).apply();
    }

}
