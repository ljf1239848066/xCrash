package com.geetest.xcrashsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.geetest.xcrash.XCrashTest;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type != null) {
            if (type.equals("native")) {
                XCrashTest.testNativeCrash(false);
            } else if (type.equals("java")) {
                XCrashTest.testJavaCrash(false);
            }
        }
    }
}
