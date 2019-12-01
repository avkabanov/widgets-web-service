package com.kabanov.widgets.interseptor.rate_limit;

/**
 * @author Kabanov Alexey
 */
public interface Refillable {

    void refill(long nextRefillAfterNanos);
}
