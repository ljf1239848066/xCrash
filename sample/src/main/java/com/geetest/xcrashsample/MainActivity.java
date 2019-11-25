// Copyright (c) 2019-present, iQIYI, Inc. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

// Created by caikelun on 2019-03-07.
package com.geetest.xcrashsample;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.geetest.xcrash.XCrashTest;

import java.io.File;
import java.math.BigDecimal;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testNativeCrashInMainThread_onClick(View view) {
        XCrashTest.testNativeCrash(false);
    }

    public void testNativeCrashInAnotherJavaThread_onClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XCrashTest.testNativeCrash(false);
            }
        }, "java_thread_with_a_very_long_name").start();
    }

    public void testNativeCrashInAnotherNativeThread_onClick(View view) {
        XCrashTest.testNativeCrash(true);
    }

    public void testNativeCrashInAnotherActivity_onClick(View view) {
        startActivity(new Intent(this, SecondActivity.class).putExtra("type", "native"));
    }

    public void testNativeCrashInAnotherProcess_onClick(View view) {
        startService(new Intent(this, MyService.class).putExtra("type", "native"));
    }

    public void testJavaCrashInMainThread_onClick(View view) {
        XCrashTest.testJavaCrash(false);
    }

    public void testJavaCrashInAnotherThread_onClick(View view) {
        XCrashTest.testJavaCrash(true);
    }

    public void testJavaCrashInAnotherActivity_onClick(View view) {
        startActivity(new Intent(this, SecondActivity.class).putExtra("type", "java"));
    }

    public void testJavaCrashInAnotherProcess_onClick(View view) {
        startService(new Intent(this, MyService.class).putExtra("type", "java"));
    }
    public void testAnrInput_onClick(View view) {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
    }

    public void testJavaCrashInSampleMainThread_onClick(View view) {
        testJavaCrash(true);
    }

    public void testJavaCrashInSampleAnotherThread_onClick(View view) {
        testJavaCrash(false);
    }

    public static void testJavaCrash(boolean runInNewThread) throws RuntimeException {
        Random random = new Random();
        int num = random.nextInt(6);
        Log.d(TAG, "testJavaCrash case:" + num);
        switch (num) {
            case 1:
            default:
                if (runInNewThread) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            throw new RuntimeException("test java exception");
                        }
                    };
                    thread.setName("xcrash_test_java_thread");
                    thread.start();
                } else {
                    throw new RuntimeException("test java exception");
                }
                break;
            case 2:
                BigDecimal bigDecimal1 = new BigDecimal(1000);
                BigDecimal bigDecimal2 = new BigDecimal(3);
                BigDecimal bigDecimal3 = bigDecimal1.divide(bigDecimal2);
                Log.d(TAG, "testJavaCrash case:2 bigDecimal3=" + bigDecimal3.toString());
                break;
            case 3:
                int f = 3;
                int g = 0;
                int h = f / g;
                Log.d(TAG, "testJavaCrash case:3 h=" + h);
                break;
            case 4:
                String str = null;
                Log.d(TAG, "testJavaCrash case:4 str=" + str.substring(3));
                break;
            case 5:
                String filename = null;
                File file = new File(filename);
                file.mkdir();
                file.listFiles();
                file.delete();
                file.setWritable(true);

                Log.d(TAG, "testJavaCrash case:5 file=" + file.getAbsolutePath());
                break;
        }
    }
}
