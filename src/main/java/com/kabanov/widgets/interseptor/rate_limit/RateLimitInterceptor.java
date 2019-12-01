package com.kabanov.widgets.interseptor.rate_limit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kabanov.widgets.interseptor.rate_limit.data.Bucket;
import com.kabanov.widgets.interseptor.rate_limit.data.ConsumptionProbe;
import com.kabanov.widgets.interseptor.rate_limit.data.Refiller;
import com.kabanov.widgets.interseptor.rate_limit.properties.RateLimitProperties;

/**
 * @author Kabanov Alexey
 */
@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private ConcurrentHashMap<String, Bucket> bucketsPerEndpoint = new ConcurrentHashMap<>();
    private Bucket defaultEndpointBucket;
    private Refiller refiller;
    private RateLimitProperties properties;

    @Autowired
    public RateLimitInterceptor(Refiller refiller, RateLimitProperties properties) {
        this.refiller = refiller;
        this.properties = properties;
        
        defaultEndpointBucket = new Bucket(properties.getDefaultRateLimit());
        refiller.addRefillable(defaultEndpointBucket);
        
        for (RateLimitProperties.Endpoint endpoint : properties.getEndpoints()) {
            Bucket bucket = new Bucket(endpoint.getRateLimit());
            bucketsPerEndpoint.put(endpoint.getPath(), bucket);
            refiller.addRefillable(bucket);
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

        if (probe.isConsumed()) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            return false;
        }
    }

    @Nonnull
    private Bucket getBucketForDefaultEndpoint() {
        return defaultEndpointBucket;
    }

    private Bucket getBucketForEndpoint(String endPoint) {
        return bucketsPerEndpoint.get(endPoint);
    }
}
