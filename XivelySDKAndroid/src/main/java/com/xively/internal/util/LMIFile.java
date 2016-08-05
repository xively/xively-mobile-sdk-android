package com.xively.internal.util;

import java.io.File;
import java.util.ArrayList;

import com.xively.internal.logger.LMILog;

public class LMIFile {

    private static final LMILog log = new LMILog("LMIFile");

    private final File mFile;

    public LMIFile(final String path) {
        // mPath = path;
        mFile = (path != null) ? new File(path) : null;
    }

    private LMIFile(final File file) {
        mFile = file;
    }

    public boolean exists() {
        return ((mFile != null) && mFile.exists());
    }

    public boolean isDirectory() {
        return ((mFile != null) && mFile.isDirectory());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean mkdir() {
        return ((mFile != null) && mFile.mkdir());
    }

    public static String getFilenameFromPath(final String path) {
        final int idx = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return path.substring(idx + 1);
    }

    private String getPath() {
        return (mFile != null) ? mFile.getPath() : null;
    }

    public static String getFileExtension(final String fileName) {
        String ret = null;

        if (fileName != null) {
            final int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex >= 0) {
                ret = fileName.substring(lastIndex + 1);
            }
        }

        if (ret == null) {
            ret = "";
        }

        return ret;
    }

    private ArrayList<LMIFile> list() {
        ArrayList<LMIFile> lmiFiles = null;
        final File[] files = (((mFile != null) && exists() && isDirectory()) ? mFile.listFiles() : null);
        if (files != null) {
            lmiFiles = new ArrayList<>();
            for (final File file : files) {
                lmiFiles.add(new LMIFile(file));
            }
        }
        return lmiFiles;
    }

    private boolean delete() {
        return ((mFile != null) && mFile.delete());
    }

    boolean deleteDirectory() {
        final ArrayList<LMIFile> files = list();
        if (files != null) {
            for (final LMIFile file : files) {
                if (file == null) {
                    continue;
                }

                if (file.isDirectory()) {
                    if (!file.deleteDirectory()) {
                        log.e("Failed to delete directory: " + file.getPath());
                    }
                } else {
                    file.delete();
                }
            }
        }

        if (!delete()) {
            log.e("Failed to delete directory: " + getPath());
            return false;
        }

        return true;
    }

    public static boolean createDirectories(final String path) {
        // TODO LM Use File.mkdirs ?
        final String[] tpaths = path.split("/");
        String tpath = "";
        for (int i = 0; i < (tpaths.length - 1); i++) {
            tpath += tpaths[i];
            if (tpath.length() > 0) {
                final LMIFile f = new LMIFile(tpath);
                if (f.exists()) {
                    if (!f.isDirectory()) {
                        log.e("File exists but not directory: " + tpath);
                        return false;
                    }
                } else {
                    if (!f.mkdir()) {
                        log.e("Failed to create directoryL " + tpath);
                        return false;
                    }
                }
            }
            tpath += "/";
        }
        return true;
    }

}
