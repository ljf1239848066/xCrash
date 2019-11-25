package com.geetest.xcrash;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.regex.Pattern;

public class CrashUploader {
    private final static String TAG = "CrashUploader";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final CrashUploader instance = new CrashUploader();

    private boolean filterInnerCrash;
    private String[] innerPkgNames;

    static CrashUploader getInstance() {
        return instance;
    }

    void initialize(boolean filterInnerCrash,
                    String[] innerPkgNames) {
        this.filterInnerCrash = filterInnerCrash;
        this.innerPkgNames = innerPkgNames;
    }

    // callback for java crash, native crash and ANR
    private ICrashCallback callback = new ICrashCallback() {
        @Override
        public void onCrash(String logPath, String emergency) {
//            Log.d(TAG, "log path: " + (logPath != null ? logPath : "(null)") + ", emergency: " + (emergency != null ? emergency : "(null)"));
            if (emergency != null) {
                debug(logPath, emergency);

                // Disk is exhausted, send crash report immediately.
                sendThenDeleteCrashLog(logPath, emergency);
            } else {
                // Add some expanded sections. Send crash report at the next time APP startup.

                // OK
                TombstoneManager.appendSection(logPath, "expanded_key_1", "expanded_content");
                TombstoneManager.appendSection(logPath, "expanded_key_2", "expanded_content_row_1\nexpanded_content_row_2");

                // Invalid. (Do NOT include multiple consecutive newline characters ("\n\n") in the content string.)
                // TombstoneManager.appendSection(logPath, "expanded_key_3", "expanded_content_row_1\n\nexpanded_content_row_2");

                debug(logPath, null);
            }
        }
    };

    public ICrashCallback getCallback() {
        return callback;
    }

    void handleLastCrash(Context ctx) {
        // Send all pending crash log files.
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File file : TombstoneManager.getAllTombstones()) {
                    sendThenDeleteCrashLog(file.getAbsolutePath(), null);
                }
            }
        }).start();
    }

    private void sendThenDeleteCrashLog(String logPath, String emergency) {
        // Parse
        Map<String, String> map = null;
        try {
            map = TombstoneParser.parse(logPath, emergency);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (map == null) {
            return;
        }

        String javaStackTrace = map.get(TombstoneParser.keyJavaStacktrace);
        String nativeStackTrace = map.get(TombstoneParser.keyBacktrace);
        Log.d(TAG, "logPath=" + logPath);
        Log.d(TAG, "curPkgName=" + Util.getPackageName(CrashUploader.class));
        Log.e(TAG, "JavaStacktrace=" + map.get(TombstoneParser.keyJavaStacktrace));

        //filter inner crash in sdk
        if (filterInnerCrash) {
            boolean isJavaInnerCrash = isJavaInnerCrash(javaStackTrace);
            XCrash.getLogger().i(TAG, "isJavaInnerCrash=" + isJavaInnerCrash);

            boolean isNativeInnerCrash = isNativeInnerCrash(nativeStackTrace);
            XCrash.getLogger().i(TAG, "isNativeInnerCrash=" + isNativeInnerCrash);
        }

        //String crashReport = new JSONObject(map).toString();

        // Send the crash report to server-side.
        // ......

        // If the server-side receives successfully, delete the log file.
        //
        // Note: When you use the placeholder file feature,
        //       please always use this method to delete tombstone files.
        //
        //TombstoneManager.deleteTombstone(logPath);
    }

    private void debug(String logPath, String emergency) {
        // Parse and save the crash info to a JSON file for debugging.
        FileWriter writer = null;
        try {
            File debug = new File(XCrash.getContext().getFilesDir() + "/tombstones/debug.json");
            debug.createNewFile();
            writer = new FileWriter(debug, false);
            writer.write(new JSONObject(TombstoneParser.parse(logPath, emergency)).toString());
        } catch (Exception e) {
            Log.d(TAG, "debug failed", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean isJavaInnerCrash(String stacktrace) {
        if (stacktrace == null) {
            return false;
        }
        String[] lines = stacktrace.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (line.startsWith("\tat ")) {
                String tmp = line.replace("\tat ", "");
                for (String pkgName : innerPkgNames) {
                    if (tmp.startsWith(pkgName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNativeInnerCrash(String stacktrace) {
        if (stacktrace == null) {
            return false;
        }
        String soName = "lib" + NativeHandler.LIBNAME + ".so";
        String[] lines = stacktrace.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (Pattern.matches("^#\\d+\\s+pc.*", line) && line.contains(soName)) {
                return true;
            }
        }
        return false;
    }
}
