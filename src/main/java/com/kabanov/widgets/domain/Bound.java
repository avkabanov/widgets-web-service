package com.kabanov.widgets.domain;

import java.awt.*;

import javax.annotation.Nonnull;

/**
 * @author Kabanov Alexey
 */
public class Bound {
    
    @Nonnull
    private Point lowerLeftPoint;
    
    private int height; 
    
    private int width;

    public Bound(@Nonnull Point lowerLeftPoint, int height, int width) {
        this.lowerLeftPoint = lowerLeftPoint;
        this.height = height;
        this.width = width;
    }

    @Nonnull
    public Point getLowerLeftPoint() {
        return lowerLeftPoint;
    }

    public void setLowerLeftPoint(@Nonnull Point lowerLeftPoint) {
        this.lowerLeftPoint = lowerLeftPoint;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
