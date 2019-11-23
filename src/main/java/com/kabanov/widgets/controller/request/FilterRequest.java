package com.kabanov.widgets.controller.request;

import java.awt.*;

import javax.validation.constraints.NotNull;

/**
 * @author Kabanov Alexey
 */
public class FilterRequest {

    @NotNull(message = "Start point can not be null")
    private Point startPoint;
    
    private int height;
    
    private int width;

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
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
