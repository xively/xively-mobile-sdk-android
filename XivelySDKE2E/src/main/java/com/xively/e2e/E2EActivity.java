package com.xively.e2e;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xively.XiException;
import com.xively.XiServiceCreatorCallback;
import com.xively.XiSession;
import com.xively.auth.XiAuthentication;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.auth.XiAuthenticationFactory;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiDeviceInfoCallback;
import com.xively.messaging.XiDeviceInfoListCallback;
import com.xively.messaging.XiDeviceUpdateCallback;
import com.xively.messaging.XiEndUserCallback;
import com.xively.messaging.XiEndUserInfo;
import com.xively.messaging.XiEndUserListCallback;
import com.xively.messaging.XiEndUserUpdateCallback;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiMessagingDataListener;
import com.xively.messaging.XiMessagingStateListener;
import com.xively.messaging.XiMessagingSubscriptionListener;
import com.xively.messaging.XiOrganizationCallback;
import com.xively.messaging.XiOrganizationInfo;
import com.xively.messaging.XiOrganizationListCallback;
import com.xively.timeseries.TimeSeriesItem;
import com.xively.timeseries.XiTimeSeries;
import com.xively.timeseries.XiTimeSeriesCallback;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class E2EActivity extends AppCompatActivity {

    private XiSession xivelySession;
    private XiMessaging xivelyMessaging;
    private List<XiEndUserInfo> endUserList;
    private List<XiDeviceInfo> deviceList;
    private List<XiOrganizationInfo> organizationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        String xivelyUsername = "";
        String xivelyPassword = "";
        String xivelyAccountId = "";

        XiAuthenticationCallback callback = new XiAuthenticationCallback() {
            @Override
            public void sessionCreated(XiSession xiSession) {
                Log.i("XISDK", "XiAuthentication success, session created");
                xivelySession = xiSession;
                listOrganizations();
            }

            @Override
            public void authenticationFailed(XiAuthenticationError xiAuthenticationError) {
                Log.i("XISDK", "XiAuthentication Failed " + xiAuthenticationError );
            }
        };

        XiAuthentication xiAuthentication = XiAuthenticationFactory.createAuthenticationService(getBaseContext());
        xiAuthentication.requestAuth(xivelyUsername, xivelyPassword, xivelyAccountId, callback );
    }

    public void listOrganizations()
    {
        xivelySession.requestXiOrganizationList(new XiOrganizationListCallback(){
            @Override
            public void onOrganizationListReceived(List<XiOrganizationInfo> list) {
                Log.i("XISDK", "Organization list received " + list );
                if (list == null || list.size() == 0){
                    Log.i("XISDK", "No organizations present");
                    listEndUsers();
                    return;
                }

                organizationList = list;
                listOrganization();
            }

            @Override
            public void onOrganizationListFailed()
            {
                Log.i("XISDK", "Organization list request failed");
                listEndUsers();
            }
        });
    }

    public void listOrganization()
    {
        XiOrganizationInfo info = organizationList.get(0);

        XiOrganizationCallback callback = new XiOrganizationCallback() {
            @Override
            public void onOrganizationReceived(XiOrganizationInfo organizationInfo) {
                Log.i("XISDK","Organization info received " + organizationInfo);
                listEndUsers();
            }

            @Override
            public void onOrganizationFailed()
            {
                Log.i("XISDK","Organization info request failed");
                listEndUsers();
            }
        };

        xivelySession.requestXiOrganization(info.organizationId,callback);
    }

    public void listEndUsers()
    {
        xivelySession.requestXiEndUserList(new XiEndUserListCallback(){
            @Override
            public void onEndUserListReceived(List<XiEndUserInfo> list) {
                Log.i("XISDK", "End user list received " + list );
                if (list == null || list.size() == 0){
                    Log.i("XISDK", "No end users present");
                    listDevices();
                    return;
                }

                endUserList = list;
                listEndUser();
            }

            @Override
            public void onEndUserListFailed()
            {
                Log.i("XISDK", "End user list request failed");
                listDevices();
            }
        });
    }

    public void listEndUser()
    {
        XiEndUserInfo info = endUserList.get(0);

        XiEndUserCallback callback = new XiEndUserCallback() {
            @Override
            public void onEndUserReceived(XiEndUserInfo userInfo) {
                Log.i("XISDK","End user info received " + userInfo);
                updateEndUser();
            }

            @Override
            public void onEndUserFailed()
            {
                Log.i("XISDK","End user info request failed");
                listDevices();
            }
        };

        xivelySession.requestXiEndUser(info.userId,callback);
    }

    public void updateEndUser()
    {
        XiEndUserInfo info = endUserList.get(0);

        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("emailAddress","aham@aham.com");

        XiEndUserUpdateCallback callback = new XiEndUserUpdateCallback() {
            @Override
            public void onEndUserUpdateSuccess(XiEndUserInfo info) {
                Log.i("XISDK","End user updated " + info );
                listDevices();
            }

            @Override
            public void onEndUserUpdateFailed()
            {
                Log.i("XISDK","End user update failed");
                listDevices();
            }
        };

        xivelySession.requestXiEndUserUpdate(info.userId,info.version,data,callback );
    }

    public void listDevices()
    {
        xivelySession.requestXiDeviceInfoList(new XiDeviceInfoListCallback() {
            @Override
            public void onDeviceInfoListReceived(List<XiDeviceInfo> list) {
                Log.i("XISDK", "Device list received " + list);
                if (list == null || list.size() == 0){
                    Log.i("XISDK", "No devices present");
                    return;
                }

                deviceList = list;
                listDevice();
            }

            @Override
            public void onDeviceInfoListFailed()
            {
                Log.i("XISDK", "Device list request failed");
            }
        });

    }

    public void listDevice()
    {
        XiDeviceInfo info = deviceList.get(0);

        XiDeviceInfoCallback callback = new XiDeviceInfoCallback() {
            @Override
            public void onDeviceInfoReceived(XiDeviceInfo deviceInfo) {
                Log.i("XISDK","Device info received " + deviceInfo);
                updateDevice();
            }

            @Override
            public void onDeviceInfoFailed()
            {
                Log.i("XISDK","Device info request failed");
            }
        };

        xivelySession.requestXiDeviceInfo(info.deviceId,callback);
    }

    public void updateDevice()
    {
        XiDeviceInfo info = deviceList.get(0);

        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("connected","false");

        XiDeviceUpdateCallback callback = new XiDeviceUpdateCallback() {
            @Override
            public void onDevicUpdateSuccess(XiDeviceInfo info) {
                Log.i("XISDK","Device info update success " + info );
                testMessaging();
            }

            @Override
            public void onDeviceUpdateFailed()
            {
                Log.i("XISDK","Device info update failed");
            }
        };

        xivelySession.requestXiDeviceUpdate(info.deviceId,info.deviceVersion,data,callback );
    }

    public String normalChannel;
    public String seriesChannel;

    public void testMessaging()
    {
        XiDeviceInfo info = deviceList.get(0);

        for (XiDeviceChannel channel : info.deviceChannels )
        {
            Log.i("XISDK","Channel " + channel);
            if ( channel.persistenceType == XiDeviceInfo.PersistenceTypeEnum.simple ) normalChannel = channel.channelId;
            else if ( channel.persistenceType == XiDeviceInfo.PersistenceTypeEnum.timeSeries ) seriesChannel = channel.channelId;
        }

        if ( normalChannel != null ) {

            XiMessagingCreator messagingCreator = xivelySession.requestMessaging();

            messagingCreator.addServiceCreatorCallback(new XiServiceCreatorCallback<XiMessaging>() {
                @Override
                public void onServiceCreated(XiMessaging xiMessaging) {
                    Log.i("XISDK", "Messaging service created " + xiMessaging);

                    xivelyMessaging = xiMessaging;
                    xivelyMessaging.addDataListener(new XiMessagingDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes, String topic) {
                            Log.i("XISDK", "Message from " + topic + " : " + new String(bytes, Charset.defaultCharset()));
                        }

                        @Override
                        public void onDataSent(int i) {
                            Log.i("XISDK", "Data sent" + i);
                        }
                    });

                    xivelyMessaging.addStateListener(new XiMessagingStateListener() {
                        @Override
                        public void onStateChanged(XiMessaging.State state) {
                            Log.i("XISDK", "Messaging service state changed " + state);
                        }

                        @Override
                        public void onError() {
                            Log.i("XISDK", "Messaging service state error");
                        }
                    });

                    xivelyMessaging.addSubscriptionListener(new XiMessagingSubscriptionListener() {
                        @Override
                        public void onSubscribed(String channelId) {
                            Log.i("XISDK", "Messaging subcribed to " + channelId);

                            if ( channelId.equals(normalChannel)) {
                                try {
                                    xivelyMessaging.publish(channelId, "TEST".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
                                } catch (XiException.NotConnectedException exception) {
                                }
                            }

                            if ( channelId.equals(seriesChannel))
                            {
                                try {

                                    Log.i("XISDK","Sending timeseries messages to " + seriesChannel);

                                    xivelyMessaging.publish(seriesChannel, ",,1,".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
                                    xivelyMessaging.publish(seriesChannel, ",,1,".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
                                    xivelyMessaging.publish(seriesChannel, " , ,1, ".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);
                                    xivelyMessaging.publish(seriesChannel, " , ,1,".getBytes(), XiMessaging.XiMessagingQoS.AtLeastOnce);

                                } catch (XiException.NotConnectedException exception) {
                                    Log.i("XISDK","Timeseries publish exception");
                                }

                                testTimeSeries(seriesChannel);
                            }
                        }

                        @Override
                        public void onSubscribeFailed(String channelId) {
                            Log.i("XISDK", "Messaging subcribe to " + channelId + " failed.");
                        }

                        @Override
                        public void onUnsubscribed(String channelId) {
                            Log.i("XISDK", "Messaging unsubcribed from " + channelId);
                        }

                        @Override
                        public void onUnsubscribeFailed(String channelId) {
                            Log.i("XISDK", "Messaging unsubcribe from " + channelId + " failed");
                        }
                    });

                    try {
                        xivelyMessaging.subscribe(normalChannel, XiMessaging.XiMessagingQoS.AtLeastOnce);
                        xivelyMessaging.subscribe(seriesChannel, XiMessaging.XiMessagingQoS.AtLeastOnce);
                    } catch (XiException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (XiException.ConnectionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceCreateFailed() {
                    Log.i("XISDK", "Messaging service creation failed");
                }
            });
            messagingCreator.createMessaging();

        }
    }

    public void testTimeSeries(String channelId)
    {
        Log.i("XISDK","Time series test " + channelId);
        XiTimeSeries timeSeries = xivelySession.timeSeries();

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US); //ISO 8601
        Log.i("XISDK","" + isoFormat.format(new Date(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000)));

        timeSeries.requestTimeSeriesItemsForChannel(channelId, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), new XiTimeSeriesCallback() {
            @Override
            public void onTimeSeriesItemsRetrieved(ArrayList<TimeSeriesItem> items) {
                Log.i("XISDK","Time series retrieve success " + items);
            }

            @Override
            public void onFinishedWithError(XiTimeSeriesError error) {
                Log.i("XISDK","Time series retrieve error " + error);
            }

            @Override
            public void onCancelled() {
                Log.i("XISDK","Time series retrieve cancel");
            }
        });

    }
}
