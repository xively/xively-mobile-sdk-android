package com.xively.demo.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.demo.XiSettings;
import com.xively.XiException;
import com.xively.XiServiceCreatorCallback;
import com.xively.XiSession;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiLastWill;
import com.xively.messaging.XiMessaging;
import com.xively.messaging.XiMessagingCreator;
import com.xively.messaging.XiMessagingDataListener;
import com.xively.messaging.XiMessagingStateListener;
import com.xively.messaging.XiMessagingSubscriptionListener;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class MessagingFragment extends Fragment {

    private static final String ARG_PARAM1 = "MessagingFragment.deviceId";
    private static final String ARG_PARAM2 = "MessagingFragment.deviceChannels";

    private String mDeviceId;
    private CharSequence[] mDeviceChannels;

    private OnFragmentInteractionListener mListener;

    private TextView tvDeviceSelector;
    private TextView tvChannelSelector;
    private TextView tvMessages;
    private String tvChannel;
    private ScrollView messagesScroll;
    private TextView tvStatus;
    private EditText editText;
    private Button btSend;

    private XiMessaging xivelyMessaging;

    public static MessagingFragment newInstance(XiDeviceInfo device) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();

        ArrayList<String> channels = new ArrayList<>();
        for (XiDeviceChannel channel: device.deviceChannels){
            channels.add(channel.channelId);
        }

        args.putString(ARG_PARAM1, device.deviceId);
        args.putStringArrayList(ARG_PARAM2, channels);

        fragment.setArguments(args);
        return fragment;
    }

    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceId = getArguments().getString(ARG_PARAM1);
            ArrayList<String> channelsArray = getArguments().getStringArrayList(ARG_PARAM2);
            mDeviceChannels = new CharSequence[channelsArray.size()];
            for(int i=0; i<channelsArray.size(); i++){
                mDeviceChannels[i] = channelsArray.get(i);
            }
        }
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

        tvDeviceSelector = (TextView) view.findViewById(R.id.Message_TextViewDeviceSelector);
        tvChannelSelector = (TextView) view.findViewById(R.id.Message_textViewChannelSelector);
        tvMessages = (TextView) view.findViewById(R.id.Message_tvMessages);
        tvStatus = (TextView) view.findViewById(R.id.Message_Status);
        editText = (EditText) view.findViewById(R.id.Message_editTextMessage);
        btSend = (Button) view.findViewById(R.id.Message_buttonSend);
        messagesScroll = (ScrollView) view.findViewById(R.id.Message_messages_scroll);

        if (TextUtils.isEmpty(mDeviceId)){
            tvDeviceSelector.setText("Tap here to select a device...");
            tvStatus.setText("");
            tvChannelSelector.setText("");
        } else {
            tvStatus.setText("");
            tvDeviceSelector.setText("Id: " + mDeviceId);
            tvChannelSelector.setText("Tap here to select a channel...");
        }

        tvDeviceSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onDevicesRequest();
                }
            }
        });

        tvChannelSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChannel();
            }
        });

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
                                XiMessaging.XiMessagingQoS.AtLeastOnce
                        );
                        editText.setText("");
                    } catch (XiException.NotConnectedException e) {
                        tvStatus.setText("Failed to send message");
                    }
                }
            }
        });

        messagesScroll.setSmoothScrollingEnabled(true);

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
                tvMessages.setText(messages + "\n" + newMessage);
                messagesScroll.invalidate();
                messagesScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void connectMessaging(){
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        XiMessagingCreator messagingCreator = session.requestMessaging();
        messagingCreator.addServiceCreatorCallback(new XiServiceCreatorCallback<XiMessaging>() {
            @Override
            public void onServiceCreated(XiMessaging xiMessaging) {
                xivelyMessaging = xiMessaging;
                tvChannelSelector.setEnabled(true);

                xivelyMessaging.addStateListener(new XiMessagingStateListener() {
                    @Override
                    public void onStateChanged(XiMessaging.State state) {
                        tvStatus.setText(state.toString());

                        if (state.equals(XiMessaging.State.Running)) {
                            enableSend();
                        } else {
                            disableSend();
                        }
                    }

                    @Override
                    public void onError() {
                        tvStatus.setText("Failed to connect");
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
                tvStatus.setText("Failed to connect");
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
        tvChannel = channel;
        XiMessagingSubscriptionListener subscriptionListener =
                new XiMessagingSubscriptionListener() {
                    @Override
                    public void onSubscribed(String s) {
                        tvChannelSelector.setText("Channel: " + tvChannel);
                        tvStatus.setText("Connected");
                        enableSend();
                    }

                    @Override
                    public void onSubscribeFailed(String s) {
                        tvStatus.setText("Failed to subscribe");
                    }

                    @Override
                    public void onUnsubscribed(String s) {
                        tvStatus.setText("Disconnected");
                    }

                    @Override
                    public void onUnsubscribeFailed(String s) {
                        tvStatus.setText("Failed to unsubscribe");
                    }
                };

        try {
            xivelyMessaging.addSubscriptionListener(subscriptionListener);
            xivelyMessaging.subscribe(
                    tvChannel,
                    XiMessaging.XiMessagingQoS.AtLeastOnce
            );
        } catch (XiException e) {
            tvStatus.setText("Failed to unsubscribe: " + e);
        }
    }

    private void selectChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select channel")
                .setItems(mDeviceChannels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        subscribeToChannel(mDeviceChannels[which].toString());
                    }
                });
        builder.create().show();
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
