package com.kabanov.widgets.dao.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;

import com.kabanov.widgets.component.bounds.InBoundCalculator;
import com.kabanov.widgets.component.bounds.InBoundCalculatorFactory;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Transactional
public class DatabaseWidgetCache implements WidgetCache {

    private WidgetRepository widgetRepository;
    private InBoundCalculatorFactory inBoundCalculatorFactory;

    @Autowired
    public DatabaseWidgetCache(WidgetRepository widgetRepository,
                               InBoundCalculatorFactory inBoundCalculatorFactory) {
        this.widgetRepository = widgetRepository;
        this.inBoundCalculatorFactory = inBoundCalculatorFactory;
    }

    @Nonnull
    @Override
    public Widget add(@Nonnull Widget widget) {
        // TODO that that widget with given id is not exist
        return widgetRepository.save(widget);
    }

    @Nonnull
    @Override
    @Lock(LockModeType.PESSIMISTIC_READ)
    public Widget updateWidget(@Nonnull UpdateWidgetRequest updateWidgetRequest) {
        Optional<Widget> result = widgetRepository.findById(updateWidgetRequest.getUuid());
        if (!result.isPresent()) {
            throw new IllegalArgumentException("Widget with UUID: " + updateWidgetRequest.getUuid() + " was not found");
        }

        Widget updatedWidget = updateWidgetRequest.createUpdatedWidget(result.get());
        updatedWidget.setLastModificationTime(LocalDateTime.now());
        widgetRepository.save(updatedWidget);
        return updatedWidget;
    }

    @Override
    public void deleteWidget(@Nonnull UUID uuid) {
        widgetRepository.deleteById(uuid);
    }

    @Nullable
    @Override
    public Widget getWidget(@Nonnull UUID uuid) {
        return widgetRepository.getOne(uuid);
    }

    @Nonnull
    @Override
    public List<Widget> getAllWidgetsSortedByLayer() {
        return widgetRepository.findAll(Sort.by(Sort.Direction.ASC, "zIndex"));
    }

    @Nonnull
    @Override
    public List<Widget> getAllWidgetsInBound(Bound bound) {
        InBoundCalculator inBoundCalculator = inBoundCalculatorFactory.getInBoundCalculator(bound);

        Slice<Widget> result = widgetRepository.findAllByStartPointSum(Pageable.unpaged());
        ;

        return null;
    }
}
