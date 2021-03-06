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
     * Default index for background widget if no other widgets exists
     */
    int DEFAULT_BACKGROUND_INDEX = 0;

    /**
     * @param widget widget to add
     * @return created widget
     * @throws IllegalArgumentException if widget with given uuid already exist
     */
    @Nonnull
    Widget add(@Nonnull Widget widget);

    /**
     * Updates all non null fields in update widgets request.
     * <p>
     * {@link Widget#getLastModificationTime()} is updated internally
     *
     * @param updateWidgetRequest
     * @return updated widget
     * @throws IllegalArgumentException if widget by id was not found
     */
    @Nonnull
    Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest);

    /**
     * @param uuid id of widget to remove
     */
    void deleteWidget(@Nonnull UUID uuid);

    @Nullable
    Widget getWidget(@Nonnull UUID uuid);

    @Nonnull
    List<Widget> getAllWidgetsSortedByLayer();

    @Nonnull
    List<Widget> getAllWidgetsSortedByLayer(int pageNumber, int pageSize);

    /**
     * @param bound
     * @return returns all widgets that fall into specific region
     */
    @Nonnull
    List<Widget> getAllWidgetsInBound(Bound bound);

    /**
     * Removes all widget
     */
    void deleteAll();
}
