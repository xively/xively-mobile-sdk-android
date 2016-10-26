package com.xively.demo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xively.XiException;
import com.xively.XiServiceCreatorCallback;
import com.xively.XiSession;
import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.demo.XiSettings;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiMessagingDataListener;
import com.xively.messaging.XiMessagingStateListener;
import com.xively.messaging.XiMessagingSubscriptionListener;
import com.xively.timeseries.TimeSeriesItem;
import com.xively.timeseries.XiTimeSeries;
import com.xively.timeseries.XiTimeSeriesCallback;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

public class MessagingFragment extends Fragment {

    private static final String ARG_PARAM1 = "MessagingFragment.deviceId";
    private static final String ARG_PARAM2 = "MessagingFragment.deviceChannels";

    private RadioButton lastWillQoS0Button;
    private RadioButton lastWillQoS1Button;
    private CheckBox lastWillRetain;

    private OnFragmentInteractionListener mListener;

    private TextView tvMessages;
    private String tvChannel;
    private ScrollView messagesScroll;
    private EditText editText;
    private Button btSend;

    public XiDeviceChannel deviceChannel;
    private XiMessaging xivelyMessaging;
    private XiTimeSeries xivelyTimeSeries;

    public static MessagingFragment newInstance(XiDeviceChannel channel) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        fragment.deviceChannel = channel;
        return fragment;
    }

    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvChannel = deviceChannel.channelId;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tvChannel.substring(tvChannel.lastIndexOf('/') + 1));

        tvMessages = (TextView) view.findViewById(R.id.Message_tvMessages);
        editText = (EditText) view.findViewById(R.id.Message_editTextMessage);
        btSend = (Button) view.findViewById(R.id.Message_buttonSend);
        messagesScroll = (ScrollView) view.findViewById(R.id.Message_messages_scroll);

        disableSend();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xivelyMessaging != null &&
                        xivelyMessaging.getState().equals(XiMessaging.State.Running)) {
                    try {
                        xivelyMessaging.publish(
                                tvChannel,
                                editText.getText().toString().getBytes(),
                                XiMessaging.XiMessagingQoS.AtLeastOnce,
                                lastWillRetain.isChecked()
                        );
                        editText.setText("");
                    } catch (XiException.NotConnectedException e) {
                    }
                }
            }
        });

        messagesScroll.setSmoothScrollingEnabled(true);


        lastWillQoS0Button = (RadioButton) view.findViewById(R.id.messaging_qos0);
        lastWillQoS1Button = (RadioButton) view.findViewById(R.id.messaging_qos1);
        lastWillRetain = (CheckBox) view.findViewById(R.id.messaging_retain);

        lastWillQoS0Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lastWillQoS1Button.setChecked(false);
                }
                else if ( xivelyMessaging != null )
                {
                    try {
                        xivelyMessaging.unsubscribe(tvChannel);
                        xivelyMessaging.subscribe(tvChannel,XiMessaging.XiMessagingQoS.AtLeastOnce);
                    } catch (XiException e) {
                        addMessage("Failed to unsubscribe: " + e);
                    }
                }
            }
        });

        lastWillQoS1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lastWillQoS0Button.setChecked(false);
                }
                else if ( xivelyMessaging != null )
                {
                    try {
                        xivelyMessaging.unsubscribe(tvChannel);
                        xivelyMessaging.subscribe(tvChannel,XiMessaging.XiMessagingQoS.AtMostOnce);
                    } catch (XiException e) {
                        addMessage("Failed to unsubscribe: " + e);
                    }
                }
            }
        });

        if ( deviceChannel.persistenceType == XiDeviceInfo.PersistenceTypeEnum.timeSeries ) requestTimeSeries();
        connectMessaging();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (xivelyMessaging != null){
            xivelyMessaging.close();
        }
    }

    private void enableSend(){
        editText.setEnabled(true);
        btSend.setEnabled(true);
        tvMessages.setEnabled(true);
    }

    private void disableSend(){
        editText.setEnabled(false);
        btSend.setEnabled(false);
        tvMessages.setEnabled(false);
    }

    private void addMessage(final String newMessage){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String messages = tvMessages.getText().toString();
                tvMessages.setText(messages + "\n" + (new Date().toString()) + " : " + newMessage);
                messagesScroll.invalidate();
                messagesScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void requestTimeSeries()
    {
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        xivelyTimeSeries = session.timeSeries();

        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(endDate.getTime() - 100 * 24 * 60 * 60 * 1000);

        xivelyTimeSeries.requestTimeSeriesItemsForChannel(
                tvChannel,
                startDate, endDate,
                new XiTimeSeriesCallback() {
                    @Override
                    public void onTimeSeriesItemsRetrieved(ArrayList<TimeSeriesItem> arrayList) {
                        if (getActivity() == null){
                            return;
                        }
                        addMessage( arrayList.size() + " item(s) found for the last 7 days" );

                        for (TimeSeriesItem item: arrayList){
                            String message = item.time + " / " + item.category + " / Value: '";
                            if (!TextUtils.isEmpty(item.numericValue)){
                                message += item.numericValue;
                            }
                            if (!TextUtils.isEmpty(item.stringValue)){
                                message += item.stringValue;
                            }
                            message += "'";
                            addMessage(message);
                        }
                    }

                    @Override
                    public void onFinishedWithError(XiTimeSeriesError xiTimeSeriesError) {
                        if (getActivity() == null){
                            return;
                        }
                        if (xiTimeSeriesError == null){
                            addMessage("Failed to get data for the selected channel");
                        } else {
                            addMessage("Failed to get data: " + xiTimeSeriesError);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        if (getActivity() == null){
                            return;
                        }
                        addMessage("Request canceled");
                    }
                }
        );

    }

    private void connectMessaging(){
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        XiMessagingCreator messagingCreator = session.requestMessaging();
        messagingCreator.addServiceCreatorCallback(new XiServiceCreatorCallback<XiMessaging>() {
            @Override
            public void onServiceCreated(XiMessaging xiMessaging) {
                xivelyMessaging = xiMessaging;
                subscribeToChannel( tvChannel );

                xivelyMessaging.addStateListener(new XiMessagingStateListener() {
                    @Override
                    public void onStateChanged(XiMessaging.State state) {
                        addMessage( state.toString() );

                        if (state.equals(XiMessaging.State.Running)) {
                            enableSend();
                        } else {
                            disableSend();
                        }
                    }

                    @Override
                    public void onError() {
                        addMessage("Failed to connect");
                    }
                });

                xivelyMessaging.addDataListener(new XiMessagingDataListener() {
                    @Override
                    public void onDataReceived(byte[] bytes, String s) {
                        addMessage(new String(bytes, Charset.defaultCharset()));
                    }

                    @Override
                    public void onDataSent(int i) {

                    }
                });

            }

            @Override
            public void onServiceCreateFailed() {
                addMessage("Failed to connect");
            }
        });

        boolean cleanSession = XiSettings.getBoolean(XiSettings.PREF_CLEAN_SESSION, true);

        XiLastWill lastWill = buildLastWillObject();
        if (lastWill == null) {
            messagingCreator.createMessaging(cleanSession);
        } else {
            messagingCreator.createMessaging(cleanSession, lastWill);
        }
    }

    private void subscribeToChannel(final String channel){
        XiMessagingSubscriptionListener subscriptionListener =
                new XiMessagingSubscriptionListener() {
                    @Override
                    public void onSubscribed(String s) {
                        addMessage("Connected");
                        enableSend();
                    }

                    @Override
                    public void onSubscribeFailed(String s) {
                        addMessage("Failed to subscribe");
                    }

                    @Override
                    public void onUnsubscribed(String s) {
                        addMessage("Disconnected");
                    }

                    @Override
                    public void onUnsubscribeFailed(String s) {
                        addMessage("Failed to unsubscribe");
                    }
                };

        try {
            xivelyMessaging.addSubscriptionListener(subscriptionListener);
            xivelyMessaging.subscribe(
                    tvChannel,
                    XiMessaging.XiMessagingQoS.AtLeastOnce
            );
        } catch (XiException e) {
            addMessage("Failed to unsubscribe: " + e);
        }
    }

    private XiLastWill buildLastWillObject(){

        String topic = XiSettings.getString(XiSettings.PREF_LAST_WILL_CHANNEL, null);
        String message = XiSettings.getString(XiSettings.PREF_LAST_WILL_MESSAGE, null);
        XiMessaging.XiMessagingQoS qos = XiMessaging.XiMessagingQoS.AtMostOnce;
        int qoSValue = XiSettings.getInt(XiSettings.PREF_LAST_WILL_QOS, 0);
        if (qoSValue != 0){
            qos = XiMessaging.XiMessagingQoS.AtLeastOnce;
        }
        boolean retain = XiSettings.getBoolean(XiSettings.PREF_LAST_WILL_RETAIN, false);
        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message)){
            return null;
        }

        return new XiLastWill(topic, message.getBytes(), qos, retain);

    }

}
