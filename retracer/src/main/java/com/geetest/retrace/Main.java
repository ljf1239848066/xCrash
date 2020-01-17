package com.geetest.retrace;

import java.io.File;

//import proguard.retrace.ReTrace;

public class Main {
    public static void main(String[] args) {
        String mapping = "res\\mapping.txt";
        String log = "res\\crash.log";

        File file1 = new File(mapping);
        File file2 = new File(log);
        System.out.println("main file1=" + file1.getAbsolutePath() + ",file2=" + file2.getAbsolutePath());
//        File file3 = new File(mapping);
        ReTrace reTrace = new ReTrace("", true, file1, file2);
        try {
            reTrace.execute();
        } catch (Exception ex) {

        }
    }
}
