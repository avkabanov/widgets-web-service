package com.kabanov.widgets.service.widget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kabanov.widgets.controller.request.CreateWidgetRequest;
import com.kabanov.widgets.controller.request.FilterRequest;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.PaginationProperties;
import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Service
public class WidgetService {

    private WidgetCache widgetCache;
    private PaginationProperties paginationProperties;

    @Autowired
    public WidgetService(WidgetCache widgetCache, PaginationProperties paginationProperties) {
        this.widgetCache = widgetCache;
        this.paginationProperties = paginationProperties;
    }

    public Widget createWidget(@Nonnull CreateWidgetRequest createWidgetRequest) {
        Widget widget = new Widget();
        widget.setUuid(UUID.randomUUID());
        widget.setStartPoint(createWidgetRequest.getStartPoint());
        widget.setHeight(createWidgetRequest.getHeight());
        widget.setWidth(createWidgetRequest.getWidth());
        widget.setZIndex(createWidgetRequest.getZIndex());
        widget.setLastModificationTime(LocalDateTime.now());

        return addWidgetToCache(widget);
    }

    public Widget addWidgetToCache(Widget widget) {
        return widgetCache.add(widget);
    }

    @Nullable
    public Widget getWidget(@Nonnull UUID uuid) {
        return widgetCache.getWidget(uuid);
    }

    @Nonnull
    public List<Widget> getAllWidgetsSortedByLayer() {
        return widgetCache.getAllWidgetsSortedByLayer();
    }

    @Nonnull
    public List<Widget> getAllWidgetsSortedByLayer(int pageNumber, @Nullable Integer pageSize) {
        if (pageSize == null) {
            pageSize = paginationProperties.getDefaultPageSize();
        }

        if (pageSize > paginationProperties.getMaxPageSize()) {
            pageSize = paginationProperties.getMaxPageSize();
        }

        return widgetCache.getAllWidgetsSortedByLayer(pageNumber, pageSize);
    }

    @Nonnull
    public Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest) {
        return widgetCache.updateWidget(updateWidgetRequest);
    }

    @Nonnull
    public List<Widget> getAllWidgetsInBound(FilterRequest filterRequest) {
        Bound bound = new Bound(filterRequest.getStartPoint(), filterRequest.getHeight(), filterRequest.getWidth());
        return widgetCache.getAllWidgetsInBound(bound);
    }

    public void deleteWidget(@Nonnull UUID uuid) {
        widgetCache.deleteWidget(uuid);
    }
}
