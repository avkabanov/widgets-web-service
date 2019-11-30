package com.kabanov.widgets.utils;

import java.awt.*;

/**
 * @author Kabanov Alexey
 */
public class PointUtils {

    private PointUtils() {
    }

    public static int getSumOfCoordinates(Point point) {
        return point.x + point.y;
    }
}
