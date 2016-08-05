package com.xively.internal.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;

import com.xively.XiSdkConfig;
import com.xively.internal.util.LMIFile;
import com.xively.internal.util.LMITime;
import com.xively.internal.Config;

public class LMILog {

    private static final boolean logStackTraces = true;

    public enum LogType {
        CONSOLE, FILE, NONE
    }

    private String mTag;
    private XiSdkConfig.LogLevel mMinLevel = XiSdkConfig.LogLevel.TRACE;

    private static XiSdkConfig.LogLevel gMinLevel = XiSdkConfig.LogLevel.TRACE;
    private static LogEvent gLogEvent;
    private static OutputStreamWriter gLogOut;
    private static String gAppTag;

    public LMILog(final String tag) {
        this(tag, XiSdkConfig.LogLevel.TRACE);
    }

    public LMILog(final String tag, final XiSdkConfig.LogLevel minLevel) {
        mTag = tag;
        mMinLevel = minLevel;
    }

    public void l(final XiSdkConfig.LogLevel level, final String msg) {
        LMILog.gLog(this, level, mTag, msg);
    }

    @SuppressWarnings("SameParameterValue")
    public void i(final String msg) {
        l(XiSdkConfig.LogLevel.INFO, msg);
    }

    public void w(final String msg) {
        l(XiSdkConfig.LogLevel.WARNING, "Warning: " + msg);
    }

    public void e(final String msg) {
        stacktrace(XiSdkConfig.LogLevel.ERROR, msg);
        Debug.DebugBreak();
    }

    public void d(final String msg) {
        l(XiSdkConfig.LogLevel.DEBUG, msg);
    }

    public void t(final String msg) {
        l(XiSdkConfig.LogLevel.TRACE, msg);
    }

    private static final Object gLogLock = new Object();
    private static LogType gLogType = LogType.CONSOLE;

    private static void gLog(final LMILog logClient, final XiSdkConfig.LogLevel level, final String tag, final String msg) {
        synchronized (gLogLock) {
            if (gLogEvent != null) {
                gLogEvent.logEvent(level, tag, msg);
            }
            if (logClient != null) {
                if (level.ordinal() < logClient.mMinLevel.ordinal()) {
                    return;
                }
            }
            if (level.ordinal() < gMinLevel.ordinal()) {
                return;
            }
            switch (gLogType) {
            case CONSOLE:
                writeConsoleLog(level, tag, msg);
                break;
            case FILE:
                final String fileLine = new LMITime().getFormatted("%1$tD %1$tT") + " [" + tag + "] " + msg + "\r\n";
                if (gLogOut != null) {
                    try {
                        gLogOut.write(fileLine);
                        gLogOut.flush();
                    } catch (final IOException e) {
                        System.err.println("log file write error: " + e);
                    }
                }
                break;
            case NONE:
                break;
            }
        }
    }

    private static void writeConsoleLog(final XiSdkConfig.LogLevel level, final String tag, final String msg) {

        String message = msg;

        if (tag != null) {
            message = "[" + tag + "] " + msg;
        }

        switch (level) {
        case WARNING:
            Log.w(gAppTag, message);
            break;
        case ERROR:
            Log.e(gAppTag, message);
            break;
        case INFO:
            Log.i(gAppTag, message);
            break;
        case DEBUG:
        case TRACE:
            Log.d(gAppTag, message);
            break;

        default:
            Log.i(gAppTag, message);
            break;
        }
    }

    public static void initLog(final LogType logType, final LogEvent logEvent, final String appTag) {
        gLogType = logType;
        gLogEvent = logEvent;
        gAppTag = appTag;
    }

    public static void initLog(final LogType logType, final XiSdkConfig.LogLevel minLevel, final LogEvent logEvent) {
        gLogType = logType;
        gMinLevel = minLevel;
        gLogEvent = logEvent;
    }

    public static void setMinLogLevel(XiSdkConfig.LogLevel logLevel){
        gMinLevel = logLevel;
    }

    public static XiSdkConfig.LogLevel getMinLogLevel(){
        return gMinLevel;
    }

    public static void setLogEvent(final LogEvent logEvent) {
        gLogEvent = logEvent;
    }

    public static boolean initLogDirectory(final String prefix, final String path) {

        // logdir
        final LMIFile logDir = new LMIFile(path);
        if (!logDir.exists()) {
            if (!logDir.mkdir()) {
                System.err.println("log error: cannot create \"logs\" directory for logs");
                return false;
            }
        } else {
            if (!logDir.isDirectory()) {
                System.err.println("log error: directory \"logs\" is a file");
                return false;
            }
        }

        final String filePath = path + "/" + prefix + "_" + new LMITime().getFormatted("%1$tY%1$tm%1$td%1$tH%1$tM") + ".log";
        try {
            gLogOut = new OutputStreamWriter(new FileOutputStream(new File(filePath)));
        } catch (final FileNotFoundException e) {
            System.err.println("log file open error: " + e);
            gLogOut = null;
            return false;
        }

        return true;
    }

    public interface LogEvent {
        void logEvent(final XiSdkConfig.LogLevel level, final String tag, final String msg);
    }

    public void exception(final Exception exception) {
        stacktrace(XiSdkConfig.LogLevel.ERROR, exception.toString());
        Debug.DebugBreak();
    }

    public void notimplemented() {
        stacktrace(XiSdkConfig.LogLevel.ERROR, "not implemented");
    }

    public void shouldnotuse() {
        stacktrace(XiSdkConfig.LogLevel.WARNING, "should not use");
    }

    void stacktrace(final XiSdkConfig.LogLevel logLevel, final String msg) {
        LMILog.gLog(this, XiSdkConfig.LogLevel.ERROR, mTag, msg);
        if (logStackTraces) {
            int line = 0;
            for (final StackTraceElement stackElement : Thread.currentThread().getStackTrace()) {
                if (line >= 3) {
                    LMILog.gLog(this, XiSdkConfig.LogLevel.ERROR, null, "    at " + stackElement.toString());
                }
                line++;
            }
        }
    }
}
