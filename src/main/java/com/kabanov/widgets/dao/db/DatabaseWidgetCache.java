package com.kabanov.widgets.dao.db;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import com.kabanov.widgets.component.bounds.InBoundCalculator;
import com.kabanov.widgets.component.bounds.InBoundCalculatorFactory;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.utils.PointUtils;
import com.kabanov.widgets.utils.WidgetUtils;

import static com.kabanov.widgets.utils.PointUtils.getSumOfCoordinates;

/**
 * @author Kabanov Alexey
 */
@Transactional
public class DatabaseWidgetCache implements WidgetCache {

    private WidgetRepository widgetRepository;
    private InBoundCalculatorFactory inBoundCalculatorFactory;
    private EntityManager entityManager;

    @Autowired
    public DatabaseWidgetCache(WidgetRepository widgetRepository,
                               InBoundCalculatorFactory inBoundCalculatorFactory,
                               EntityManager entityManager) {
        this.widgetRepository = widgetRepository;
        this.inBoundCalculatorFactory = inBoundCalculatorFactory;
        this.entityManager = entityManager;
    }

    @Nonnull
    @Override
    public Widget add(@Nonnull Widget widget) {
        Widget result; 
        if (WidgetUtils.isBackgroundWidget(widget)) {
            result = doInsertToBackground(widget);
        } else {
            result = doInsertWithShift(widget);
        }

        return result;
    }

    private void checkUuidDoesNotExist(Widget widget) {
        Widget existingWidget = getWidget(widget.getUuid());
        if (existingWidget != null) {
            throw new IllegalArgumentException("Widget with given UUID already exist: " + existingWidget);
        }
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private Widget doInsertWithShift(Widget widget) {
        checkUuidDoesNotExist(widget);

        Widget conflictedWidget = widgetRepository.findOneByZIndex(widget.getZIndex());

        if (conflictedWidget == null) {
            widgetRepository.save(widget);
        } else {
            int gap = findFirstGapFrom(conflictedWidget.getZIndex());
            shiftAllWidgetsLayerByZIndex(conflictedWidget.getZIndex(), gap);
            widgetRepository.save(widget);
        }
        return widget;
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private void shiftAllWidgetsLayerByZIndex(int zIndexFromInclusive, int zIndexToInclusive) {
        Query query = entityManager.createQuery(
                "UPDATE WIDGET " +
                        "SET zIndex = zIndex + 1 " +
                        "WHERE zIndex >= :zIndexFrom " +
                        "AND zIndex <= :zIndexTo"
        );

        query.setParameter("zIndexFrom", zIndexFromInclusive);
        query.setParameter("zIndexTo", zIndexToInclusive);
        query.executeUpdate();
    }

    private int findFirstGapFrom(int index) {
        // TODO update table name?
        Query query = entityManager.createQuery(
                "SELECT t1.zIndex " +
                        "FROM WIDGET as t1 LEFT JOIN WIDGET AS t2 ON t1.zIndex + 1 = t2.zIndex " +
                        "WHERE t2.zIndex is NULL AND t1.zIndex > :zIndex " +
                        "ORDER BY t1.zIndex"
        );
        query.setMaxResults(1);
        query.setParameter("zIndex", index);
        return (int) query.getSingleResult();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private Widget doInsertToBackground(Widget widget) {
        checkUuidDoesNotExist(widget);
        
        Integer result = findSmallestZIndex();
        if (result == null) {
            result = DEFAULT_BACKGROUND_INDEX;
        }

        widget.setZIndex(result);
        widgetRepository.save(widget);
        return widget;
    }

    /**
     * @return null if database is empty
     */
    @Nullable
    private Integer findSmallestZIndex() {
        Query query = entityManager.createQuery(
                "SELECT zIndex " +
                        "FROM WIDGET " +
                        "ORDER BY zIndex"

        );
        query.setMaxResults(1);

        List result = query.getResultList();
        return result.isEmpty() ? null : (Integer) result.get(0);

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
        Optional<Widget> result = widgetRepository.findById(uuid);
        return result.orElse(null);
    }

    @Nonnull
    @Override
    public List<Widget> getAllWidgetsSortedByLayer() {
        return widgetRepository.findAll(Sort.by(Sort.Direction.ASC, "zIndex"));
    }

    @Nonnull
    @Override
    public List<Widget> getAllWidgetsSortedByLayer(int pageNumber, int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return widgetRepository.findAll(pageRequest).getContent();
    }

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Nonnull
    @Override
    public List<Widget> getAllWidgetsInBound(Bound bound) {
        InBoundCalculator inBoundCalculator = inBoundCalculatorFactory.getInBoundCalculator(bound);
        Point upperRightPoint = bound.calculateUpperRightPoint();
        int sumOfUpperRightCoordinates = getSumOfCoordinates(upperRightPoint);
        
        List<Widget> result = new ArrayList<>();
        int currentPage = 0;
        while (true) {
            Page<Widget> page = findAllSortedByStartPointSum(currentPage++);
            if (page == null) break;

            for (Widget current : page.getContent()) {
                if (PointUtils.getSumOfCoordinates(current.getStartPoint()) > sumOfUpperRightCoordinates) {
                    break;
                }
                if (inBoundCalculator.isInBound(current.getStartPoint(), current.getHeight(), current.getWidth())) {
                    result.add(current);
                }
            }
        }
        return result;
    }

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Nullable
    private Page<Widget> findAllSortedByStartPointSum(int currentPage) {
        // retrieve widgets one by one from database
        final PageRequest pageRequest = PageRequest.of(currentPage, 1, Sort.by("startPointSum").ascending());
        Page<Widget> page = widgetRepository.findAll(pageRequest);

        if (!page.hasContent()) {
            return null;
        }
        return page;
    }

    @Override
    public void deleteAll() {
        widgetRepository.deleteAll();
    }
}
                                                