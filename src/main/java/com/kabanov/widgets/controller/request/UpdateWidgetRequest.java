package com.kabanov.widgets.controller.request;

import java.awt.*;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.kabanov.widgets.domain.Widget;

/**
 * In update request we specify all the fields that possible to change. If field is null - means field leave with no
 * changes
 *
 * @author Kabanov Alexey
 */
public class UpdateWidgetRequest {

    @NotNull(message = "UUID can not be null")
    private UUID uuid;

    @Nullable
    private Point startPoint;

    @Nullable
    @Positive(message = "Height must be positive")
    private Integer height;

    @Nullable
    @Positive(message = "Width must be positive")
    private Integer width;

    @Nullable
    private Integer zIndex;

    public UpdateWidgetRequest() {
    }

    public UpdateWidgetRequest(@NotNull(message = "UUID can not be null") UUID uuid, @Nullable Point startPoint,
                               @Nullable @Positive(message = "Height must be positive")
                                       Integer height, @Nullable @Positive(
            message = "Width must be positive") Integer width, @Nullable Integer zIndex) {
        this.uuid = uuid;
        this.startPoint = startPoint;
        this.height = height;
        this.width = width;
        this.zIndex = zIndex;
    }

    public Widget createUpdatedWidget(@Nonnull Widget widget) {
        Widget updatedWidget = new Widget(widget);
        
        if (startPoint != null) updatedWidget.setStartPoint(startPoint);
        if (height != null) updatedWidget.setHeight(height);
        if (width != null) updatedWidget.setWidth(width);
        if (zIndex != null) updatedWidget.setZIndex(zIndex);
        
        return updatedWidget;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Nullable
    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(@Nullable Point startPoint) {
        this.startPoint = startPoint;
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    @Nullable
    public Integer getWidth() {
        return width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    @Nullable
    public Integer getzIndex() {
        return zIndex;
    }

    public void setzIndex(@Nullable Integer zIndex) {
        this.zIndex = zIndex;
    }
}
