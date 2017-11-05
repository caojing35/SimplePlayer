package com.mustache.simpleplayer.util;

import android.os.SystemClock;

import java.util.ArrayList;

import static java.lang.System.out;

/**
 * Created by caojing on 2017/11/5.
 */

public class ClockCount {
    private StringBuilder builder = new StringBuilder();

    private String name;

    private long last;

    private long start;

    public ClockCount(String name) {
        last = System.nanoTime();// SystemClock.elapsedRealtimeNanos();
        start = last;
        name = name;
        builder.append(name + "\n");
    }

    public void addClick(String action) {
        long current = System.nanoTime();//SystemClock.elapsedRealtimeNanos();
        long elapse = current - last;
        last = current;
        builder.append(action + ":" + elapse + "\n");
    }

    public String desc()
    {
        builder.append("end:" + (System.nanoTime()/*SystemClock.elapsedRealtimeNanos()*/ - start));
        return builder.toString();
    }
}
