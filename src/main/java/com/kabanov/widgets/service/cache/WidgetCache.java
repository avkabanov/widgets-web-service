package com.kabanov.widgets.service.cache;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    public Widget add(Widget widget, boolean isBackgroundWidget) {
        if (widget == null) {
            throw new IllegalArgumentException("Widget can not be null");
        }

        uuidWidgetMap.computeIfAbsent(widget.getUuid(), w -> {
            widgetLayersStorage.add(widget, isBackgroundWidget);
            return widget;
        });
        
        return widget;
    }

    public int getBackgroundIndex() {
        return widgetLayersStorage.getBackgroundIndex();
    }
}
