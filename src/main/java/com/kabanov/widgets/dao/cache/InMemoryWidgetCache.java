package com.kabanov.widgets.dao.cache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;

import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
public class InMemoryWidgetCache implements WidgetCache {

    private WidgetLayersStorage widgetLayersStorage;
    private WidgetPositionStorage widgetPositionStorage;

    private ConcurrentHashMap<UUID, Widget> uuidWidgetMap = new ConcurrentHashMap<>();

    @Autowired
    public InMemoryWidgetCache(WidgetLayersStorage widgetLayersStorage,
                               WidgetPositionStorage widgetPositionStorage) {
        this.widgetLayersStorage = widgetLayersStorage;
        this.widgetPositionStorage = widgetPositionStorage;
    }

    @Nonnull
    @Override
    public Widget add(@Nonnull Widget widget) {
        return uuidWidgetMap.compute(widget.getUuid(), (uuid, currentValue) -> {
            if (currentValue != null) {
                throw new IllegalArgumentException("Can not add widget with UUID: " + widget.getUuid() + ". " +
                        "Widget with such id already exist: " + currentValue);
            } else {
                widgetPositionStorage.add(widget);
                return widgetLayersStorage.add(widget);

            }
        });
    }

    @Override
    @Nonnull
    public Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest) {
        return uuidWidgetMap.compute(updateWidgetRequest.getUuid(), (uuid, existingWidget) -> {
            if (existingWidget == null) {
                throw new IllegalArgumentException(
                        "Widget with UUID: " + updateWidgetRequest.getUuid() + " was not found");
            }
            Widget updatedWidget = updateWidgetRequest.createUpdatedWidget(existingWidget);
            updatedWidget.setLastModificationTime(LocalDateTime.now());

            widgetPositionStorage.update(existingWidget, updatedWidget);
            widgetLayersStorage.update(existingWidget, updatedWidget);
            return updatedWidget;
        });
    }

    @Override
    public void deleteWidget(@Nonnull UUID uuid) {
        uuidWidgetMap.compute(uuid, (key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("Can not delete widget with UUID: " + uuid + ". " +
                        "Widget with such id do not exist");
            } else {
                widgetPositionStorage.remove(value);
                widgetLayersStorage.remove(value);
                return null;
            }
        });
    }

    @Override
    @Nullable
    public Widget getWidget(@Nonnull UUID uuid) {
        return uuidWidgetMap.get(uuid);
    }

    @Override
    @Nonnull
    public List<Widget> getAllWidgetsSortedByLayer() {
        return widgetLayersStorage.getAllWidgetsSortedByLayer();
    }

    @Override
    @Nonnull
    public List<Widget> getAllWidgetsInBound(Bound bound) {
        return widgetPositionStorage.getWidgetsInBound(bound);
    }

    @Override
    public void deleteAll() {
        for (UUID uuid : uuidWidgetMap.keySet()) {
            uuidWidgetMap.computeIfPresent(uuid, ((uuid1, widget) -> {
                widgetLayersStorage.remove(widget);
                widgetPositionStorage.remove(widget);
                return null;
            }));
        }
    }
}
