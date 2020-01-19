package com.geetest.retrace;

import java.io.File;
import java.io.FilenameFilter;

import proguard.retrace.ReTrace;

public class Main {
    public static void main(String[] args) {
        TimeCalculator.with().setEnable(true);
        String mapping = "res\\mapping.txt";
        String log = "res\\crash.log";
        File res = new File("res");
        File[] files = res.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        });

        File fileMapping = new File(mapping);
        System.out.println("main fileMapping=" + fileMapping.getAbsolutePath());

        for (File file : files) {
            String name = file.getName();
            System.out.println("main file=" + file.getAbsolutePath());
            TimeCalculator.with().startMethod(name);
            ReTrace reTrace = new ReTrace(ReTrace.STACK_TRACE_EXPRESSION, true, fileMapping, file);
            try {
                reTrace.execute();
            } catch (Exception ex) {

            }TimeCalculator.with().endMethod(name);
        }
    }
}
