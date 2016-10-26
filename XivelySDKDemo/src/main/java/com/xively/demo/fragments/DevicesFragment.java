package com.xively.demo.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xively.XiSession;
import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.messaging.XiDeviceInfo;
import com.xively.messaging.XiDeviceInfoListCallback;
import com.xively.messaging.XiOrganizationInfo;
import com.xively.messaging.XiOrganizationListCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private List<XiDeviceInfo> deviceInfoList;
    private List<XiOrganizationInfo> orgInfoList;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleAdapter adapter;
    private ArrayAdapter<DeviceItem> mAdapter;
    private List<Map<String,String>> mapData;
    private ArrayList<Object> items;
    private String actualOrg;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actualOrg = null;

        mAdapter = new ArrayAdapter<DeviceItem>(getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1);

        mapData = new ArrayList<Map<String, String>>();

        items = new ArrayList<Object>();

        adapter = new SimpleAdapter(getActivity(), mapData,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "date"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        mListView.setAdapter( adapter );

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Organizations / Devices");

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

        queryXivelyOrganizations( );
    }

    private void queryXivelyOrganizations( )
    {
        XiSession session = ((MainActivity) getActivity()).getXivelySession();
        if (session == null || !session.getState().equals(XiSession.State.Active)){
            return;
        }

        session.requestXiOrganizationList(new XiOrganizationListCallback() {
            @Override
            public void onOrganizationListReceived(List<XiOrganizationInfo> list)
            {
                if (list == null || list.size() == 0){
                    tvStatus.setText("No devices found for your account.");
                    return;
                }

                orgInfoList = list;
                queryXivelyDevices();
            }

            @Override
            public void onOrganizationListFailed() {
                tvStatus.setText("Failed to query devices.");
            }
        });
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

                deviceInfoList = list;
                filter();
            }

            @Override
            public void onDeviceInfoListFailed() {
                tvStatus.setText("Failed to query devices.");
            }
        });
    }

    private void filter()
    {
        if ( items == null ) items = new ArrayList<Object>();
        items.clear();
        mapData.clear();

        ArrayList<Object> orgitems = new ArrayList<Object>();
        ArrayList<Map<String,String>> orgdatums = new ArrayList<Map<String,String>>();
        ArrayList<Object> devitems = new ArrayList<Object>();
        ArrayList<Map<String,String>> devdatums = new ArrayList<Map<String,String>>();

        for (XiOrganizationInfo org : orgInfoList){
            String orgName = "n/a";
            if (org.name!= null &&
                    !org.name.equals("")){
                orgName = org.name;
            }

            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", org.name);
            datum.put("date", org.organizationId);

            if ( ( org.parentId == null && actualOrg == null ) || (org.parentId != null && org.parentId.equals(actualOrg) ) )
            {
                orgdatums.add(datum);
                orgitems.add(org);
            }
        }

        for (XiDeviceInfo device : deviceInfoList){
            String deviceName = "n/a";
            if (device.deviceName != null &&
                    !device.deviceName.equals("")){
                deviceName = device.deviceName;
            }

            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", device.deviceName);
            datum.put("date", device.deviceId);

            if ( device.customFields.get("organizationId").equals(actualOrg)) {
                devdatums.add(datum);
                devitems.add(device);
            }
        }

        Map<String, String> datum = new HashMap<String, String>(2);

        datum.put("title", "ORGANIZATIONS (" + orgitems.size() + ")" );

        mapData.add(datum);
        items.add("ORGS");

        mapData.addAll( orgdatums );
        items.addAll( orgitems );

        datum = new HashMap<String, String>(2);
        datum.put("title", "DEVICES (" + devitems.size() + ")" );

        mapData.add(datum);
        items.add("DEVS");

        mapData.addAll( devdatums );
        items.addAll( devitems );

        tvStatus.setText(items.size() + " organizations(s) and device(s):");

        mListView.invalidate();
        adapter.notifyDataSetChanged();
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
        Object info = items.get(position);

        Class myclass = info.getClass();
        Class otherclass = XiOrganizationInfo.class;

        if (info.getClass().equals(XiOrganizationInfo.class)) {
            final XiOrganizationInfo orgInfo = (XiOrganizationInfo) info;
            actualOrg = orgInfo.organizationId;
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(orgInfo.name);
            filter();
        }
        if (info.getClass().equals(com.xively.messaging.XiDeviceInfo.class)) {
            final XiDeviceInfo deviceInfo = (XiDeviceInfo)info;
            String deviceInfoTitle = "Device details: ";
            String deviceInfoMessage = deviceInfo.toString();

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

    public void onBackPressed()
    {
        if ( actualOrg != null )
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Organizations / Devices");

            int index = actualOrg.lastIndexOf('/');
            if ( index > -1 ) actualOrg = actualOrg.substring( 0  , index );
            else  actualOrg = null;
        }
        filter();
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
