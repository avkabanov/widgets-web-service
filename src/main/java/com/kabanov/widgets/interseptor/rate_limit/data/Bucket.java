package com.kabanov.widgets.interseptor.rate_limit.data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kabanov Alexey
 */
public class Bucket implements Refillable {

    private AtomicInteger tokens = new AtomicInteger();
    private int maxTokens;
    private long nextRefillMillis;

    public Bucket(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public void refill(long nextRefillMillis) {
        this.nextRefillMillis = nextRefillMillis;
        tokens.set(maxTokens);
    }

    public ConsumptionProbe tryConsume(int consume) {
        AtomicBoolean applied = new AtomicBoolean(false);
        int result = tokens.accumulateAndGet(consume, (prev, next) -> {
            if (consume <= prev) {
                applied.set(true);
                return prev - consume;
            } else {
                applied.set(false);
                // remain the previous value;
                return prev;
            }
        });

        long millisToWaitForRefill = nextRefillMillis - System.currentTimeMillis();
        if (applied.get()) {
            return new ConsumptionProbe(true, result, millisToWaitForRefill);
        } else {
            return new ConsumptionProbe(false, result, millisToWaitForRefill);
        }
    }

    public int getMaxTokens() {
        return maxTokens;
    }
}
