package com.kabanov.widgets.test_utils;

/**
 * @author Kabanov Alexey
 */
public class TimeUtils {

    public static void sleepNanos(int nanos) {

        long until = System.nanoTime() + nanos;
        long now;
        do {
            now = System.nanoTime();
        } while (now <= until);
    }
}
