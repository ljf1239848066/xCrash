package com.geetest.xcrashsample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geetest.xcrash.XCrashTest;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String type = intent.getStringExtra("type");
        if (type != null) {
            if (type.equals("native")) {
                XCrashTest.testNativeCrash(false);
            } else if (type.equals("java")) {
                XCrashTest.testJavaCrash(false);
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
