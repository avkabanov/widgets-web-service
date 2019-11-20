package com.kabanov.widgets.service.cache;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Widget add(Widget widget) {
        if (widget == null) {
            throw new IllegalArgumentException("Widget can not be null");
        }
        return uuidWidgetMap.computeIfAbsent(widget.getUuid(), w -> widgetLayersStorage.add(widget));
    }
}
