package com.kabanov.widgets.service.cache;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.cache.validator.UpdateWidgetValidator;

/**
 * @author Kabanov Alexey
 */
@Component
public class WidgetCache {

    private WidgetLayersStorage widgetLayersStorage;
    private UpdateWidgetValidator updateWidgetValidator;

    private ConcurrentHashMap<UUID, Widget> uuidWidgetMap = new ConcurrentHashMap<>();

    @Autowired
    public WidgetCache(WidgetLayersStorage widgetLayersStorage, UpdateWidgetValidator updateWidgetValidator) {
        this.updateWidgetValidator = updateWidgetValidator;
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

    public Widget updateWidget(@Nonnull UUID uuid, @Nonnull Widget updatedWidget) throws ValidationException {
        Widget widget = uuidWidgetMap.get(uuid);
        if (widget == null) {
            throw new IllegalArgumentException("Widget with UUID: " + uuid + " was not found");
        }
        updateWidgetValidator.validate(widget, updatedWidget);

        uuidWidgetMap.computeIfPresent(uuid, (uuid1, oldWidget) -> {
            widgetLayersStorage.update(oldWidget, updatedWidget);
            return updatedWidget;
        });

        return updatedWidget;
    }
}
