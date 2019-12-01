package com.kabanov.widgets.interseptor.rate_limit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Kabanov Alexey
 */
@Component
@RefreshScope
@ConfigurationProperties("rate-limits")
public class RateLimitProperties {
    
    private int refillIntervalMillis;
    private int defaultRateLimit;
    private List<Endpoint> endpoints = new ArrayList<>();

    public int getRefillIntervalMillis() {
        return refillIntervalMillis;
    }

    public void setRefillIntervalMillis(int refillIntervalMillis) {
        this.refillIntervalMillis = refillIntervalMillis;
    }

    public int getDefaultRateLimit() {
        return defaultRateLimit;
    }

    public void setDefaultRateLimit(int defaultRateLimit) {
        this.defaultRateLimit = defaultRateLimit;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoint {
        private String path;
        private int rateLimit;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }
    }

    
    
}
