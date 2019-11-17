package com.kabanov.widgets.service;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kabanov.widgets.controller.create_widget.CreateWidgetRequest;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.cache.WidgetCache;

/**
 * @author Kabanov Alexey
 */
@Service
public class WidgetService {

    private WidgetCache widgetCache;

    @Autowired
    public WidgetService(WidgetCache widgetLayersStorage) {
        this.widgetCache = widgetLayersStorage;
    }

    public Widget createWidget(@Nonnull CreateWidgetRequest createWidgetRequest) {
        Widget widget = new Widget();
        widget.setUuid(UUID.randomUUID());
        widget.setStartPoint(createWidgetRequest.getStartPoint());
        widget.setHeight(createWidgetRequest.getHeight());
        widget.setWidth(createWidgetRequest.getWidth());

        boolean isBackgroundWidget = createWidgetRequest.getZIndex() == null;
        
        if (isBackgroundWidget) {
            widget.setZIndex(widgetCache.getBackgroundIndex());
        } else {
            widget.setZIndex(createWidgetRequest.getZIndex());
        }
        return addWidgetToCache(widget, isBackgroundWidget);
    }
    
    public Widget addWidgetToCache(Widget widget, boolean isBackgroundWidget) {
        return widgetCache.add(widget, isBackgroundWidget);   
    }

    
    
}
