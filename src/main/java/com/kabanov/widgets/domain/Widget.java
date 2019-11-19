package com.kabanov.widgets.domain;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Kabanov Alexey
 */
public class Widget {

    private UUID uuid;
    private Point startPoint;
    private int height;
    private int width;
    private int zIndex;
    private LocalDateTime lastModificationTime;

    public Widget() {
    }

    public Widget(UUID uuid, Point startPoint, int height, int width, int zIndex,
                  LocalDateTime lastModificationTime) {
        this.uuid = uuid;
        this.startPoint = startPoint;
        this.height = height;
        this.width = width;
        this.zIndex = zIndex;
        this.lastModificationTime = lastModificationTime;
    }

    public Widget(Widget widget) {
        this.uuid = widget.uuid;
        this.startPoint = widget.startPoint;
        this.height = widget.height;
        this.width = widget.width;
        this.zIndex = widget.zIndex;
        this.lastModificationTime = widget.lastModificationTime;
        
    }
    
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public LocalDateTime getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(LocalDateTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Widget widget = (Widget) o;
        return height == widget.height &&
                width == widget.width &&
                zIndex == widget.zIndex &&
                Objects.equals(uuid, widget.uuid) &&
                Objects.equals(startPoint, widget.startPoint) &&
                Objects.equals(lastModificationTime, widget.lastModificationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, startPoint, height, width, zIndex, lastModificationTime);
    }

    @Override
    public String toString() {
        return "Widget{" +
                "uuid=" + uuid +
                ", startPoint=" + startPoint +
                ", height=" + height +
                ", width=" + width +
                ", zIndex=" + zIndex +
                ", lastModificationTime=" + lastModificationTime +
                '}';
    }
}

