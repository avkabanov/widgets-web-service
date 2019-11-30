package com.kabanov.widgets.dao.cache;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kabanov.widgets.component.bounds.InBoundCalculator;
import com.kabanov.widgets.component.bounds.InBoundCalculatorFactory;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.utils.LockUtils;
import com.kabanov.widgets.utils.PointUtils;

/**
 * @author Kabanov Alexey
 */
@Repository
public class WidgetPositionStorage {

    private InBoundCalculatorFactory inBoundCalculatorFactory;

    @Autowired
    public WidgetPositionStorage(InBoundCalculatorFactory inBoundCalculatorFactory) {
        this.inBoundCalculatorFactory = inBoundCalculatorFactory;
    }

    private ConcurrentSkipListSet<Widget> widgetsByPosition = new ConcurrentSkipListSet<>(
            Comparator.comparingInt((Widget o) -> (o.getStartPoint().x + o.getStartPoint().y))
                    .thenComparing(Widget::getUuid));

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock updateTreeLock = readWriteLock.writeLock();
    private Lock accessTreeLock = readWriteLock.readLock();

    public void add(Widget widget) {
        LockUtils.executeInLock(accessTreeLock, () -> widgetsByPosition.add(widget));
    }

    public void update(Widget oldWidget, Widget newWidget) {
        LockUtils.executeInLock(updateTreeLock, () -> {
            widgetsByPosition.remove(oldWidget);
            widgetsByPosition.add(newWidget);
        });
    }

    public void remove(Widget value) {
        LockUtils.executeInLock(accessTreeLock, () -> widgetsByPosition.remove(value));
    }

    @Nonnull
    public List<Widget> getWidgetsInBound(@Nonnull Bound bounds) {
        List<Widget> result = new ArrayList<>();
        InBoundCalculator inBoundCalculator = inBoundCalculatorFactory.getInBoundCalculator(bounds);
        Point upperRightPoint = bounds.calculateUpperRightPoint();
        int sumOfUpperRightCoordinates = PointUtils.getSumOfCoordinates(upperRightPoint);

        LockUtils.executeInLock(accessTreeLock, () -> {
            for (Widget current : widgetsByPosition) {
                if (PointUtils.getSumOfCoordinates(current.getStartPoint()) > sumOfUpperRightCoordinates) {
                    break;
                }
                if (inBoundCalculator.isInBound(current.getStartPoint(), current.getHeight(), current.getWidth())) {
                    result.add(current);
                }
            }
        });
        return result;
    }
}
