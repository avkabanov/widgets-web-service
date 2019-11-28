package com.kabanov.widgets.domain;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Kabanov Alexey
 */
@Table(indexes = { @Index(name = "IDX_START_POINT_SUM", columnList = "startPointSum") })
@Entity
public class Widget {
    @Id
    private UUID uuid;
    private Point startPoint;
    private int height;
    private int width;
    private Integer zIndex;
    private LocalDateTime lastModificationTime;

    private Integer startPointSum;
    
    public Widget() {
    }

    public Widget(UUID uuid, Point startPoint, int height, int width, Integer zIndex,
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
    
    @Column
    public int getStartPointSum() {
        if (startPointSum == null) {
            startPointSum = startPoint.x + startPoint.y;
        }
        return startPointSum;
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

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
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
                Objects.equals(zIndex, widget.zIndex) &&
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

