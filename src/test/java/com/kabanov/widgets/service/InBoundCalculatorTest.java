package com.kabanov.widgets.service;

import java.awt.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.service.bounds.InBoundCalculator;
import com.kabanov.widgets.service.bounds.InBoundCalculatorFactory;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = InBoundCalculatorFactory.class)
public class InBoundCalculatorTest {

    @Autowired
    private InBoundCalculatorFactory inBoundCalculatorFactory;
    private Bound bound = new Bound(new Point(0, 0), 5, 10);
    
    private InBoundCalculator calculator;
   
    @Before
    public void setup() {
        calculator = inBoundCalculatorFactory.getInBoundCalculator(bound);
    }
    
    @Test
    public void shouldReturnTrueWhenRectangleFullyInside() {
        boolean result = calculator.isInBound(new Point(1, 1), 1, 2);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenRectangleMatchTheBound() {
        boolean result = calculator.isInBound(new Point(0, 0), 5, 10);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenRectanglePartyInside() {
        boolean result = calculator.isInBound(new Point(1, 1), 2, 100);
        Assert.assertFalse(result);
    }
    
    public void shouldReturnFalseWhenRectangleIsNotInside() {
        boolean result = calculator.isInBound(new Point(10, 10), 1, 1);
        Assert.assertFalse(result);
    }
}