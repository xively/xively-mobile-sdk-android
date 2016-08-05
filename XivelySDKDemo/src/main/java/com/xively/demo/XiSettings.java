package com.xively.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class XiSettings {

    //preference keys
    public static final String PREF_LOG_LEVEL = "log_level";
    public static final String PREF_CLEAN_SESSION = "clean_session";
    public static final String PREF_LAST_WILL_CHANNEL = "last_will_channel";
    public static final String PREF_LAST_WILL_MESSAGE = "last_will_message";
    public static final String PREF_LAST_WILL_QOS = "last_will_qos";
    public static final String PREF_LAST_WILL_RETAIN = "last_will_retain";
    public static final String PREF_SDK_ENVIRONMENT = "sdk_environment";

    private static final String PREF_NAME = XiSettings.class.getName();
    private static Context mAppContext;

    private XiSettings() {
    }

    public static void init(final Context context) {
        mAppContext = context;
    }

    private static SharedPreferences getPreferences() {
        return (mAppContext != null) ? mAppContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) : null;
    }

    // get boolean
    public static boolean getBoolean(final String id, final boolean def) {
        final SharedPreferences prefs = getPreferences();
        if ((prefs == null) || !prefs.contains(id)) {
            return def;
        }
        return prefs.getBoolean(id, def);
    }

    // get int
    public static int getInt(final String id, final int def) {
        final SharedPreferences prefs = getPreferences();
        if ((prefs == null) || !prefs.contains(id)) {
            return def;
        }
        return prefs.getInt(id, def);
    }

    // get long
    public static long getLong(final String id, final long def) {
        final SharedPreferences prefs = getPreferences();
        if ((prefs == null) || !prefs.contains(id)) {
            return def;
        }
        return prefs.getLong(id, def);
    }

    // get string
    public static String getString(final String id, final String def) {
        final SharedPreferences prefs = getPreferences();
        if ((prefs == null) || !prefs.contains(id)) {
            return def;
        }
        return prefs.getString(id, def);
    }

    // set boolean
    public static void setBoolean(final String id, final boolean value) {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.putBoolean(id, value);
                editor.apply();
            }
        }
    }

    // set int
    public static void setInt(final String id, final int value) {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.putInt(id, value);
                editor.commit();
            }
        }
    }

    // set long
    public static void setLong(final String id, final long value) {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.putLong(id, value);
                editor.commit();
            }
        }
    }

    // set String
    public static void setString(final String id, final String value) {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.putString(id, value);
                editor.commit();
            }
        }
    }

    //clears all settings
    public static void reset() {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.clear();
                editor.apply();
            }
        }
    }

    public static void clear(final String pref) {
        final SharedPreferences prefs = getPreferences();
        if (prefs != null) {
            final Editor editor = prefs.edit();
            if (editor != null) {
                editor.remove(pref);
                editor.commit();
            }
        }
    }

}
