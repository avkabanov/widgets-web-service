package com.kabanov.widgets.service.cache;

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
import org.springframework.stereotype.Component;

import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.bounds.InBoundCalculator;
import com.kabanov.widgets.service.bounds.InBoundCalculatorFactory;
import com.kabanov.widgets.utils.LockUtils;

/**
 * @author Kabanov Alexey
 */
@Component
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
        Point upperRightPoint = new Point(bounds.getLowerLeftPoint().x + bounds.getWidth(),
                bounds.getLowerLeftPoint().y + bounds.getHeight());
        int sumOfUpperRightCoordinates = getSumOfCoordinates(upperRightPoint);

        LockUtils.executeInLock(accessTreeLock, () -> {
            for (Widget current : widgetsByPosition) {
                if (getSumOfCoordinates(current.getStartPoint()) > sumOfUpperRightCoordinates) {
                    break;
                }
                if (inBoundCalculator.isInBound(current.getStartPoint(), current.getHeight(), current.getWidth())) {
                    result.add(current);
                }
            }
        });
        return result;
    }

    private int getSumOfCoordinates(Point point) {
        return point.x + point.y;
    }
}
