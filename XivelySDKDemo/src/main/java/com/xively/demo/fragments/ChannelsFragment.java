package com.xively.demo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xively.demo.R;
import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ChannelsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_PARAM1 = "MessagingFragment.deviceId";
    private static final String ARG_PARAM2 = "MessagingFragment.deviceChannels";

    private OnFragmentInteractionListener mListener;

    private String mDeviceId;
    private String mDeviceName;
    private CharSequence[] mDeviceChannels;
    public ArrayList<XiDeviceChannel> deviceChannels;

    private TextView tvStatus;
    private AlertDialog alertDialog;
    public XiDeviceInfo deviceInfo;
    //private List<DeviceItem> xiDevices = new ArrayList<>();
    private List<XiDeviceInfo> deviceInfoList;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<ChannelItem> mAdapter;
    private List<Map<String,String>> mapData;

    public static ChannelsFragment newInstance(XiDeviceInfo device) {
        ChannelsFragment fragment = new ChannelsFragment();
        Bundle args = new Bundle();

        fragment.deviceChannels = device.deviceChannels;
        fragment.mDeviceName = device.deviceName;

        ArrayList<String> channels = new ArrayList<>();
        for (XiDeviceChannel channel: device.deviceChannels){
            channels.add(channel.channelId);
        }

        args.putString(ARG_PARAM1, device.deviceId);
        args.putStringArrayList(ARG_PARAM2, channels);

        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChannelsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<ChannelItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1);

        mapData = new ArrayList<Map<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStatus = (TextView) view.findViewById(R.id.devices_status);

        if (getArguments() != null)
        {
            mDeviceId = getArguments().getString(ARG_PARAM1);

            for(int i=0; i<deviceChannels.size(); i++)
            {
                XiDeviceChannel channel = deviceChannels.get(i);
                mAdapter.add(
                        new ChannelItem(channel.channelId,channel.channelId));

                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("title", channel.channelId.substring(channel.channelId.lastIndexOf('/') + 1) );
                datum.put("date", channel.persistenceType == XiDeviceInfo.PersistenceTypeEnum.simple ? "Simple" : "TimeSeries" );
                mapData.add(datum);

            }

            //mAdapter.notifyDataSetChanged();
            tvStatus.setText("This device has access to " + deviceChannels.size() + " channels(s):");

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), mapData,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "date"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});

            mListView.setAdapter( adapter );
            mListView.invalidate();
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mDeviceName);
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
        if (alertDialog != null &&
                alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final XiDeviceChannel channel = deviceChannels.get(position);

        mListener.onMessagingRequest(channel);

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * An item representing a device in the list.
     */
    private class ChannelItem {
        public String id;
        public String content;

        public ChannelItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}
