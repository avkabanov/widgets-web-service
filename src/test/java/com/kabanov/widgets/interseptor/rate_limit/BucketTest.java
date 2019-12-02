package com.kabanov.widgets.interseptor.rate_limit;

import org.junit.Assert;
import org.junit.Test;

import com.kabanov.widgets.interseptor.rate_limit.data.Bucket;
import com.kabanov.widgets.interseptor.rate_limit.data.ConsumptionProbe;

/**
 * @author Kabanov Alexey
 */
public class BucketTest {

    @Test
    public void shouldReturnMaxTokensOnRefill() {
        Bucket bucket = new Bucket(10);
        bucket.refill(System.currentTimeMillis());
        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());

        bucket.refill(System.currentTimeMillis());
        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());
    }

    @Test
    public void shouldNotBeAbleToConsumeMoreTokensThanExist() {
        Bucket bucket = new Bucket(10);
        bucket.refill(System.currentTimeMillis());

        Assert.assertTrue(bucket.tryConsume(10).isConsumed());
        Assert.assertFalse(bucket.tryConsume(1).isConsumed());
    }

    @Test
    public void shouldReturnValidRemainingTokensOnConsume() {
        Bucket bucket = new Bucket(10);
        bucket.refill(System.currentTimeMillis());

        ConsumptionProbe result = bucket.tryConsume(1);
        Assert.assertEquals(9, result.getRemainingTokens());
        Assert.assertTrue(result.isConsumed());
    }

    @Test
    public void shouldBeAbleToConsumeAllTokens() {
        Bucket bucket = new Bucket(10);
        bucket.refill(System.currentTimeMillis());

        ConsumptionProbe result = bucket.tryConsume(10);
        Assert.assertEquals(0, result.getRemainingTokens());
        Assert.assertTrue(result.isConsumed());
    }

    @Test
    public void shouldNotBeAbleToConsumeMoreThanExistingTokens() {
        Bucket bucket = new Bucket(10);
        bucket.refill(System.currentTimeMillis());

        ConsumptionProbe result = bucket.tryConsume(11);
        Assert.assertFalse(result.isConsumed());
        Assert.assertEquals(10, result.getRemainingTokens());
    }
}