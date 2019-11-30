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

import com.kabanov.widgets.utils.PointUtils;

/**
 * @author Kabanov Alexey
 */
// TODO double check indexes!!
@Table(name = Widget.TABLE_NAME, indexes = { @Index(name = "IDX_START_POINT_SUM", columnList = "startPointSum") })
@Entity(name = Widget.TABLE_NAME)
public class Widget {
    
    public static final String TABLE_NAME= "WIDGET";
    public static final String Z_INDEX_COLUMN_NAME = "zIndex";
    
    @Id
    private UUID uuid;
    private Point startPoint;
    private int height;
    private int width;
                         
    // TODO check column unique
    @Column(unique = true, name=Z_INDEX_COLUMN_NAME)  
    private Integer zIndex;
    private LocalDateTime lastModificationTime;

    @Column(name = "startPointSum")
    private Integer startPointSum;
    
    public Widget() {
    }

    public Widget(UUID uuid, Point startPoint, int height, int width, Integer zIndex,
                  LocalDateTime lastModificationTime) {
        this.uuid = uuid;
        this.startPoint = startPoint;
        startPointSum = PointUtils.getSumOfCoordinates(startPoint);
        this.height = height;
        this.width = width;
        this.zIndex = zIndex;
        this.lastModificationTime = lastModificationTime;
    }

    public Widget(Widget widget) {
        this.uuid = widget.uuid;
        this.startPoint = widget.startPoint;
        startPointSum = PointUtils.getSumOfCoordinates(startPoint);
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
        startPointSum = PointUtils.getSumOfCoordinates(startPoint);
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

    public Integer getStartPointSum() {
        return startPointSum;
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

