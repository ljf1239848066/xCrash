package xcrash;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class CrashUploader {
    private final static String TAG = "CrashUploader";

    // callback for java crash, native crash and ANR
    private static ICrashCallback callback = new ICrashCallback() {
        @Override
        public void onCrash(String logPath, String emergency) {
            Log.d(TAG, "log path: " + (logPath != null ? logPath : "(null)") + ", emergency: " + (emergency != null ? emergency : "(null)"));

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

    public static ICrashCallback getCallback() {
        return callback;
    }

    static void handleLastCrash(Context ctx) {
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

    private static void sendThenDeleteCrashLog(String logPath, String emergency) {
        // Parse
        Map<String, String> map = null;
        try {
            map = TombstoneParser.parse(logPath, emergency);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (map != null) {
            Log.d(TAG, "logPath=" + logPath);
            Log.d(TAG, "curPkgName=" + XCrash.getCurrentPackageName());
            Log.d(TAG, map.get(TombstoneParser.keyJavaStacktrace));
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

    private static void debug(String logPath, String emergency) {
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
}
