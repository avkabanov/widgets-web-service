package com.kabanov.widgets.interseptor.rate_limit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kabanov Alexey
 */
public class BucketTest {

    @Test
    public void shouldReturnMaxTokensOnRefill() {
        Bucket bucket = new Bucket(10);
        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());
        
        bucket.refill(System.currentTimeMillis());
        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());
    }

    @Test
    public void shouldNotBeAbleToConsumeMoreTokensThanExist() {
        Bucket bucket = new Bucket(10);
        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());
    }

}