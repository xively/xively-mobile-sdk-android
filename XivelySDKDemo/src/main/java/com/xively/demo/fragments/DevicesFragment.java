package com.xively.demo.fragments;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.xively.XiSession;
import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiDeviceInfoListCallback;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DevicesFragment extends Fragment implements AbsListView.OnItemClickListener {
    private OnFragmentInteractionListener mListener;

    private TextView tvStatus;
    private AlertDialog alertDialog;
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
    private ArrayAdapter<DeviceItem> mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<DeviceItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Devices");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStatus = (TextView) view.findViewById(R.id.devices_status);
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

        queryXivelyDevices();
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
        if (null != mListener &&
                null != deviceInfoList) {
            final XiDeviceInfo deviceInfo = deviceInfoList.get(position);
            String deviceInfoTitle = "Device details: ";
            String deviceInfoMessage =
                    "Name: " + deviceInfo.deviceName + "\n" +
                    "Id: " + deviceInfo.deviceId + "\n" +
                    "Location: " + deviceInfo.deviceLocation + "\n" +
                    "Serial number: " + deviceInfo.serialNumber + "\n" +
                    "Device version: " + deviceInfo.deviceVersion + "\n" +
                    "Purchase date: " + deviceInfo.purchaseDate + "\n" +
                    "Provisioning state: " + deviceInfo.provisioningState;

            //mListener.onDialogRequest(deviceInfoHeader, deviceInfoText);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder
                    .setTitle(deviceInfoTitle)
                    .setMessage(deviceInfoMessage)
                    .setCancelable(true)
                    .setPositiveButton("Channels",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    mListener.onChannelsRequest(deviceInfo);
                                }
                            })
                    .setNeutralButton("Back",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });


            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
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

    private void queryXivelyDevices(){
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        if (session == null || !session.getState().equals(XiSession.State.Active)){
            return;
        }

        session.requestXiDeviceInfoList(new XiDeviceInfoListCallback() {
            @Override
            public void onDeviceInfoListReceived(List<XiDeviceInfo> list) {
                if (list == null || list.size() == 0){
                    tvStatus.setText("No devices found for your account.");
                    return;
                }

                tvStatus.setText("You have access to " + list.size() + " device(s):");

                deviceInfoList = list;

                for (XiDeviceInfo device : list){
                    String deviceName = "n/a";
                    if (device.deviceName != null &&
                            !device.deviceName.equals("")){
                        deviceName = device.deviceName;
                    }
                    mAdapter.add(
                            new DeviceItem(device.deviceId,
                                    deviceName + " - " + device.deviceId)
                    );
                }

                mAdapter.notifyDataSetChanged();
                mListView.invalidate();
            }

            @Override
            public void onDeviceInfoListFailed() {
                tvStatus.setText("Failed to query devices.");
            }
        });
    }

    /**
     * An item representing a device in the list.
     */
    private class DeviceItem {
        public String id;
        public String content;

        public DeviceItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}
