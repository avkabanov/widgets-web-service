package com.kabanov.widgets.utils;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * @author Kabanov Alexey
 */
public class LockUtils {
    private LockUtils() {
    }

    public static <T> void executeInLock(Lock lock, Runnable code) {
        lock.lock();
        try {
            code.run();
        } finally {
            lock.unlock();
        }
    }

    public static <T> T executeInLock(Lock lock, Supplier<T> code) {
        lock.lock();
        try {
            return code.get();
        } finally {
            lock.unlock();
        }
    }
}
