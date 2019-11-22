package com.kabanov.widgets.controller.request;

import java.awt.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Kabanov Alexey
 */
public class CreateWidgetRequest {

    @NotNull(message = "Start point can not be null")
    private Point startPoint;

    @Positive(message = "Height must be positive")
    private int height;

    @Positive(message = "Width must be positive")
    private int width;
    
    @Nullable
    private Integer zIndex;

    public CreateWidgetRequest() {
    }

    public CreateWidgetRequest(
            @NotNull(message = "Start point can not be null") Point startPoint,
            @Positive(message = "Height must be positive") int height,
            @Positive(message = "Width must be positive") int width, @Nullable Integer zIndex) {
        this.startPoint = startPoint;
        this.height = height;
        this.width = width;
        this.zIndex = zIndex;
    }

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

    @Nullable
    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(@Nullable Integer zIndex) {
        this.zIndex = zIndex;
    }
}
