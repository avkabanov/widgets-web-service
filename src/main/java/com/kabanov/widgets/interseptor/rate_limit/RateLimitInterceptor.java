package com.kabanov.widgets.interseptor.rate_limit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Kabanov Alexey
 */
@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private ConcurrentHashMap<String, Bucket> bucketsPerEndpoint = new ConcurrentHashMap<>();
    private Bucket defaultEndpointBucket;
    private Refill refill;
    private RateLimitProperties properties;

    @Autowired
    public RateLimitInterceptor(Refill refill, RateLimitProperties properties) {
        this.refill = refill;
        this.properties = properties;
        
        defaultEndpointBucket = new Bucket(properties.getDefaultRateLimit());
        refill.addRefillable(defaultEndpointBucket);
        
        for (RateLimitProperties.Endpoint endpoint : properties.getEndpoints()) {
            Bucket bucket = new Bucket(endpoint.getRateLimit());
            bucketsPerEndpoint.put(endpoint.getPath(), bucket);
            refill.addRefillable(bucket);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String endPoint = request.getServletPath();

        Bucket bucket = getBucketForEndpoint(endPoint);
        if (bucket == null) {
            bucket = getBucketForDefaultEndpoint();
        }

        ConsumptionProbe probe = bucket.tryConsume(1);

        response.addHeader("X-Rate-Limit-Limit", Long.toString(bucket.getMaxTokens()));
        response.addHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
        response.addHeader("X-Rate-Limit-Reset",
                Long.toString(TimeUnit.MILLISECONDS.toSeconds(probe.getMillisToWaitForRefill())));

        return probe.isConsumed();
    }

    @Nonnull
    private Bucket getBucketForDefaultEndpoint() {
        return defaultEndpointBucket;
    }

    private Bucket getBucketForEndpoint(String endPoint) {
        return bucketsPerEndpoint.get(endPoint);
    }
}
