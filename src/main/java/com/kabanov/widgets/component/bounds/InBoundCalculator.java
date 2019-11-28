package com.kabanov.widgets.component.bounds;

import java.awt.*;
import java.awt.geom.Area;

import javax.annotation.Nonnull;

import com.kabanov.widgets.domain.Bound;

/**
 * @author Kabanov Alexey
 */
public class InBoundCalculator {

    private Area area;

    public InBoundCalculator(@Nonnull Bound bound) {
        area = new Area(new Rectangle(
                bound.getLowerLeftPoint().x, 
                bound.getLowerLeftPoint().y, 
                bound.getWidth(),
                bound.getHeight()));
    }

    public boolean isInBound(@Nonnull Point bottomLeftPoint, int height, int width) {
        return area.contains(bottomLeftPoint.x, bottomLeftPoint.y, width, height);
    }
}
