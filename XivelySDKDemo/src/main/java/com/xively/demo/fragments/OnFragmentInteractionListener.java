package com.xively.demo.fragments;

import android.content.DialogInterface;

import com.xively.messaging.XiDeviceChannel;
import com.xively.messaging.XiDeviceInfo;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnFragmentInteractionListener {
    void onDialogRequest(String title, String message);
    void onDialogRequest(String title, String message, DialogInterface.OnClickListener onClickListener);
    void onChannelsRequest(XiDeviceInfo device);
    void onMessagingRequest(XiDeviceChannel device);
    void onDevicesRequest();
}
