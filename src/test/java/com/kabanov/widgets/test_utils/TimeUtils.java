package com.kabanov.widgets.test_utils;

/**
 * @author Kabanov Alexey
 */
public class TimeUtils {

    public static void sleepMillis(int nanos) {

        long until = System.nanoTime() + nanos * 1000000;
        long now;
        do {
            now = System.nanoTime();
        } while (now <= until);
    }
}
