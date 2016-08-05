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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.XiSession;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;
import com.xively.timeseries.TimeSeriesItem;
import com.xively.timeseries.XiTimeSeries;
import com.xively.timeseries.XiTimeSeriesCallback;

import java.util.ArrayList;
import java.util.Date;

public class TimeSeriesFragment extends Fragment {

    private static final String ARG_PARAM1 = "TimeSeriesFragment.deviceId";
    private static final String ARG_PARAM2 = "TimeSeriesFragment.deviceChannels";

    private String mDeviceId;
    private CharSequence[] mDeviceChannels;

    private OnFragmentInteractionListener mListener;

    private TextView tvDeviceSelector;
    private TextView tvChannelSelector;
    private TextView tvMessages;
    private ScrollView messagesScroll;
    private TextView tvStatus;
    private ProgressBar progressBar;

    private XiTimeSeries xivelyTimeSeries;

    public static TimeSeriesFragment newInstance(XiDeviceInfo device) {
        TimeSeriesFragment fragment = new TimeSeriesFragment();
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

    public TimeSeriesFragment() {
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
        return inflater.inflate(R.layout.fragment_time_series, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tvDeviceSelector = (TextView) view.findViewById(R.id.TimeSeries_textViewDeviceSelector);
        tvChannelSelector = (TextView) view.findViewById(R.id.TimeSeries_textViewChannelSelector);
        tvMessages = (TextView) view.findViewById(R.id.TimeSeries_textViewMessages);
        tvStatus = (TextView) view.findViewById(R.id.TimeSeries_Status);
        messagesScroll = (ScrollView) view.findViewById(R.id.TimeSeries_messages_scroll);

        if (TextUtils.isEmpty(mDeviceId)){
            tvDeviceSelector.setText("Tap here to select a device...");
            tvStatus.setText("");
            tvChannelSelector.setText("");
            tvChannelSelector.setEnabled(false);
        } else {
            tvStatus.setText("");
            tvDeviceSelector.setText("Id: " + mDeviceId);
            tvChannelSelector.setText("Tap here to select a channel...");
            tvChannelSelector.setEnabled(true);
        }

        tvDeviceSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
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

        messagesScroll.setSmoothScrollingEnabled(true);

        connectTS();
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

    private void connectTS(){
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        xivelyTimeSeries = session.timeSeries();
        tvStatus.setText("Please select a persisted device channel...");
    }

    private void getTimeSeriesHistory(String channel){
        tvStatus.setText("Loading data...");
        progressBar.setVisibility(View.VISIBLE);

        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(endDate.getTime() - 100 * 24 * 60 * 60 * 1000);

        xivelyTimeSeries.requestTimeSeriesItemsForChannel(
                channel,
                startDate, endDate,
                new XiTimeSeriesCallback() {
                    @Override
                    public void onTimeSeriesItemsRetrieved(ArrayList<TimeSeriesItem> arrayList) {
                        if (getActivity() == null){
                            return;
                        }
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText(arrayList.size() + " item(s) found for the last 7 days");

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
                        progressBar.setVisibility(View.GONE);
                        if (xiTimeSeriesError == null){
                            tvStatus.setText("Failed to get data for the selected channel");
                        } else {
                            tvStatus.setText("Failed to get data: " + xiTimeSeriesError);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        if (getActivity() == null){
                            return;
                        }
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText("Request canceled");
                    }
                }
        );
    }

    private void selectChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select channel")
                .setItems(mDeviceChannels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tvChannelSelector.setText(mDeviceChannels[which]);
                        getTimeSeriesHistory(mDeviceChannels[which].toString());
                    }
                });
        builder.create().show();
    }

}
