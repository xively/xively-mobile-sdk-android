package com.xively.internal.device.impl;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xively.internal.DependencyInjector;
import com.xively.internal.device.DeviceInfo;

public class DeviceInfoImpl implements DeviceInfo {
	
	private String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	public synchronized String getUUId() {
	    if (uniqueID == null) {
	    	Context context = DependencyInjector.get().getContext();
	    	
	        SharedPreferences sharedPrefs = context.getSharedPreferences(
	                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
	        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
	        if (uniqueID == null) {
	            uniqueID = UUID.randomUUID().toString();
	            Editor editor = sharedPrefs.edit();
	            editor.putString(PREF_UNIQUE_ID, uniqueID);
	            editor.apply();
	        }
	    }
	    return uniqueID;
	}

}
