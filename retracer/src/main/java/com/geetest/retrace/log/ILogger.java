package com.geetest.retrace.log;

/**
 * description Logger interface
 * author      Created by lxzh
 * date        2019-09-28
 */
public interface ILogger {
    int VERBOSE = 2;
    int DEBUG = 3;
    int INFO = 4;
    int WARN = 5;
    int ERROR = 6;
    int ASSERT = 7;

    ILogger setLevel(int level);
    ILogger setTag(String tag);

    void v(String msg);

    void d(String msg);

    void i(String msg);

    void w(String msg);

    void e(String msg);

    void a(String msg);

    void printStacktrace(Exception e);

    void v(String TAG, String msg);

    void d(String TAG, String msg);

    void i(String TAG, String msg);

    void w(String TAG, String msg);

    void e(String TAG, String msg);

    void a(String TAG, String msg);

    void printStacktrace(String TAG, Exception e);
}
