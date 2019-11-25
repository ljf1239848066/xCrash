package com.geetest.xcrash;

import android.util.Log;

import java.io.File;
import java.math.BigDecimal;
import java.util.Random;

public class XCrashTest {
    private final static String TAG = "XCrash";
    /**
     * Force a java exception.
     *
     * <p>Warning: This method is for testing purposes only. Don't call it in a release version of your APP.
     *
     * @param runInNewThread Whether it is triggered in the current thread.
     * @throws RuntimeException This exception will terminate current process.
     */
    @SuppressWarnings("unused")
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

    /**
     * Force a native crash.
     *
     * <p>Warning: This method is for testing purposes only. Don't call it in a release version of your APP.
     *
     * @param runInNewThread Whether it is triggered in the current thread.
     */
    @SuppressWarnings("unused")
    public static void testNativeCrash(boolean runInNewThread) {
        NativeHandler.getInstance().testNativeCrash(runInNewThread);
    }
}
