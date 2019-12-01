package com.kabanov.widgets.interseptor.rate_limit.data;

/**
 * @author Kabanov Alexey
 */
public interface Refillable {

    void refill(long nextRefillAfterNanos);
}
