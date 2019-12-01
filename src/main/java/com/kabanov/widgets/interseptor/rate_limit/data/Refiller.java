package com.kabanov.widgets.interseptor.rate_limit.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Kabanov Alexey
 */
@Component
public class Refiller {
    
    private List<Refillable> refillables = new CopyOnWriteArrayList<>();
    private long refillIntervalMillis;

    @Autowired
    public Refiller(@Value("${rate-limits.refill-interval-millis}") long refillIntervalMillis) {
        this.refillIntervalMillis = refillIntervalMillis;
    }

    @Scheduled(fixedRateString = "${rate-limits.refill-interval-millis}")
    public void refill() {
        long nextTimeRefill = calculateNextRefillTimeMillis();
        
        for (Refillable refillable : refillables) {
            refillable.refill(nextTimeRefill);
        }
    }
    
    public void addRefillable(Refillable refillable) {
        refillables.add(refillable);     
    }
    
    public long calculateNextRefillTimeMillis() {
        return System.currentTimeMillis() + refillIntervalMillis;
    }
}
