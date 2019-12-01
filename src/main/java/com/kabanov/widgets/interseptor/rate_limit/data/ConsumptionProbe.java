package com.kabanov.widgets.interseptor.rate_limit.data;

/**
 * @author Kabanov Alexey
 */
public class ConsumptionProbe {

    private boolean consumed;
    private int remainingTokens;
    private long millisToWaitForRefill;

    public ConsumptionProbe() {
    }

    public ConsumptionProbe(boolean consumed, int remainingTokens, long millisToWaitForRefill) {
        this.consumed = consumed;
        this.remainingTokens = remainingTokens;
        this.millisToWaitForRefill = millisToWaitForRefill;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public int getRemainingTokens() {
        return remainingTokens;
    }

    public void setRemainingTokens(int remainingTokens) {
        this.remainingTokens = remainingTokens;
    }

    public long getMillisToWaitForRefill() {
        return millisToWaitForRefill;
    }

    public void setMillisToWaitForRefill(long millisToWaitForRefill) {
        this.millisToWaitForRefill = millisToWaitForRefill;
    }
}
