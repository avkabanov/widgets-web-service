package com.kabanov.widgets.dao;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
public interface WidgetCache {

    /**
     * 
     * @param widget
     * @return created widget
     * @throws IllegalArgumentException if widget with given uuid already exist
     */
    @Nonnull
    Widget add(@Nonnull Widget widget);

    /**
     * @param updateWidgetRequest
     * @return updated widget
     * @throws IllegalArgumentException if widget by id was not found
     */
    @Nonnull
    Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest);

    void deleteWidget(@Nonnull UUID uuid);

    @Nullable
    Widget getWidget(@Nonnull UUID uuid);

    @Nonnull
    List<Widget> getAllWidgetsSortedByLayer();

    @Nonnull
    List<Widget> getAllWidgetsInBound(Bound bound);
}
