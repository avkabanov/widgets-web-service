package com.kabanov.widgets.domain;

import java.awt.*;
import java.time.LocalDateTime;
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
    private LocalDateTime creationDateTime;

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

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
