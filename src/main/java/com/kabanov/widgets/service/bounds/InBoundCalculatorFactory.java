package com.kabanov.widgets.service.bounds;

import org.springframework.stereotype.Component;

import com.kabanov.widgets.domain.Bound;

/**
 * @author Kabanov Alexey
 */
@Component
public class InBoundCalculatorFactory {
    
    public InBoundCalculator getInBoundCalculator(Bound bound) {
        return new InBoundCalculator(bound);
    }
}
