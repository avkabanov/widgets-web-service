package com.kabanov.widgets.service.cache;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Component
public class WidgetCache {

    private WidgetLayersStorage widgetLayersStorage;

    private ConcurrentHashMap<UUID, Widget> uuidWidgetMap = new ConcurrentHashMap<>();

    @Autowired
    public WidgetCache(WidgetLayersStorage widgetLayersStorage) {
        this.widgetLayersStorage = widgetLayersStorage;
    }

    @Nonnull
    public Widget add(@Nonnull Widget widget) {
        return uuidWidgetMap.compute(widget.getUuid(), (uuid, currentValue) -> {
            if (currentValue != null) {
                throw new IllegalArgumentException("Can not add widget with UUID: " + widget.getUuid() + ". " +
                        "Widget with such id already exist: " + currentValue);    
            } else {
                return widgetLayersStorage.add(widget);    
            }
        });
    }

    @Nullable
    public Widget getWidget(@Nonnull UUID uuid) {
        return uuidWidgetMap.get(uuid);
    }

    @Nonnull
    public List<Widget> getAllWidgetsSortedByLayer() {
        return widgetLayersStorage.getAllWidgetsSortedByLayer();
    }

    @Nonnull
    public Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest) {
        
        return uuidWidgetMap.compute(updateWidgetRequest.getUuid(), (uuid, existingWidget) -> {
            if (existingWidget == null) {
                throw new IllegalArgumentException("Widget with UUID: " + updateWidgetRequest.getUuid() + " was not found");
            }
            Widget updatedWidget = updateWidgetRequest.createUpdatedWidget(existingWidget);
            
            widgetLayersStorage.update(existingWidget, updatedWidget);
            return updatedWidget;
        });
    }

    public void removeWidget(@Nonnull UUID uuid) {
        uuidWidgetMap.compute(uuid, (key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("Can not delete widget with UUID: " + uuid + ". " +
                        "Widget with such id do not exist");
            } else {
                widgetLayersStorage.remove(value);
                return null;
            }
        });
    }
}
