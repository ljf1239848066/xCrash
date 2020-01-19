package com.geetest.retrace;

import com.geetest.retrace.log.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 函数级时间统计工具
 *
 * @author geetest 雷进锋
 * @date 2019/12/12
 */
public class TimeCalculator {
    class TimeItem {
        long methodStart;
        long methodEnd;

        long getMiliSeconds() {
            return methodEnd - methodStart;
        }

        public TimeItem(long methodStart) {
            this.methodStart = methodStart;
        }
    }

    private volatile static TimeCalculator timeCalculator;

    private Map<String, TimeItem> timeItemMap;

    private boolean isEnable = false;

    private TimeCalculator() {
        timeItemMap = new HashMap<>();
    }

    public static TimeCalculator with() {
        if (timeCalculator == null) {
            synchronized (TimeCalculator.class) {
                if (timeCalculator == null) {
                    timeCalculator = new TimeCalculator();
                }
            }
        }
        return timeCalculator;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void startMethod(String method) {
        if (isEnable) {
            timeItemMap.put(method, new TimeItem(System.currentTimeMillis()));
        }
    }

    public void endMethod(String method) {
        if (isEnable && timeItemMap.containsKey(method)) {
            TimeItem item = timeItemMap.get(method);
            item.methodEnd = System.currentTimeMillis();
            long timeElapsed = item.getMiliSeconds();
            Logger.get().v("Method:" + method + " timeElapsed=" + timeElapsed + "ms");
            timeItemMap.remove(item);
        }
    }
}
