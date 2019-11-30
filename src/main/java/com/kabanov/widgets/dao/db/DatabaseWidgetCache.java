package com.kabanov.widgets.dao.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import com.kabanov.widgets.component.bounds.InBoundCalculator;
import com.kabanov.widgets.component.bounds.InBoundCalculatorFactory;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.utils.WidgetUtils;


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
        if (WidgetUtils.isBackgroundWidget(widget)) {
            doInsertToBackground(widget);
        } else {
            doInsertWithShift(widget);
        }

        // TODO that that widget with given id is not exist
        return widgetRepository.save(widget);
    }

    private void checkUuidDoesNotExist(Widget widget) {
        Widget existingWidget = getWidget(widget.getUuid());
        if (existingWidget != null) {
            throw new IllegalArgumentException("Widget with given UUID already exist: " + existingWidget);
        }
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private void doInsertWithShift(Widget widget) {
        checkUuidDoesNotExist(widget);
        
        Widget conflictedWidget = widgetRepository.findOneByZIndex(widget.getZIndex());

        if (conflictedWidget == null) {
            widgetRepository.save(widget);
        } else {
            int gap = findFirstGapFrom(conflictedWidget.getZIndex());
            shiftAllWidgetsLayerByZIndex(conflictedWidget.getZIndex(), gap);
            widgetRepository.save(widget);
        }
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void shiftAllWidgetsLayerByZIndex(int zIndexFromInclusive, int zIndexToInclusive) {
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

    public int findFirstGapFrom(int index) {
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

    /*
    	SELECT q1.position
	    FROM queue as q1 LEFT JOIN queue AS q2 ON q1.position + 1 = q2.position 
	    WHERE q2.name is NULL AND q1.position > 3
	    ORDER BY q1.position
	    LIMIT 1
     */
    /*public int findTheNearestGapAfter(int index) {
        final String leftTableAlias = "t1";
        final String rightTableAlias = "t2";
        final String leftTableZIndex = leftTableAlias + "." + Widget.Z_INDEX_COLUMN_NAME;
        final String rightTableZIndex = rightTableAlias + "." + Widget.Z_INDEX_COLUMN_NAME;

        ;

        entityManager.createQuery(query).getSingleResult();
        return 1;
    }*/

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private void doInsertToBackground(Widget widget) {
        Integer result = findTop1ByZIndex();
        if (result == null) {
            result = DEFAULT_BACKGROUND_INDEX;
        }

        widget.setZIndex(result);
        widgetRepository.save(widget);
    }

    private Integer findTop1ByZIndex() {
        Query query = entityManager.createQuery(
                "SELECT zIndex " +
                        "FROM WIDGET " +
                        "ORDER BY zIndex"

        );
        query.setMaxResults(1);

        List result = query.getResultList();
        return result.isEmpty() ? null : (Integer)result.get(0) ;

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
    public List<Widget> getAllWidgetsInBound(Bound bound) {
        InBoundCalculator inBoundCalculator = inBoundCalculatorFactory.getInBoundCalculator(bound);

        Slice<Widget> result = widgetRepository.findAllByStartPointSum(Pageable.unpaged());
        ;

        return null;
    }

    @Override
    public void deleteAll() {
        widgetRepository.deleteAll();
    }
}
                                                