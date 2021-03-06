package com.kabanov.widgets.dao.cache;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Repository;

import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.utils.LockUtils;
import com.kabanov.widgets.utils.WidgetUtils;

/**
 * @author Kabanov Alexey
 */
@Repository
public class WidgetLayersStorage {

    private ConcurrentSkipListSet<Widget> widgetsByLayer = new ConcurrentSkipListSet<>(
            Comparator.comparingInt(Widget::getZIndex));

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock updateTreeLock = readWriteLock.writeLock();
    private Lock accessTreeLock = readWriteLock.readLock();

    public Widget add(Widget widget) {
        if (WidgetUtils.isBackgroundWidget(widget)) {
            doInsertToBackground(widget);
        } else {
            doInsertWithShift(widget);
        }
        return widget;
    }

    private void doInsertWithShift(Widget widget) {
        AtomicBoolean inserted = new AtomicBoolean(false);

        inserted.set(tryInsertOnEmptyIndex(widget));
        if (!inserted.get()) {
            LockUtils.executeInLock(updateTreeLock, () -> {
                // try again inside lock. Maybe z index of conflicted element has been changed in another thread
                inserted.set(widgetsByLayer.add(widget));
                if (!inserted.get()) {
                    shiftUpperWidgets(widget);
                    widgetsByLayer.add(widget);
                }
            });
        }
    }

    public void update(Widget oldWidget, Widget updatedWidget) {
        LockUtils.executeInLock(updateTreeLock, () -> {
            widgetsByLayer.remove(oldWidget);
            doInsertWithShift(updatedWidget);
        });
    }

    /**
     * Trying to insert with read-lock assuming given index is empty. We need read-lock because other thread can be in
     * the process of rebuilding the tree and we don't want to insert because at that moment tree is not guarantee to in
     * consistent state
     *
     * @param widget widget to insert
     * @return true if element was inserted
     */
    private boolean tryInsertOnEmptyIndex(Widget widget) {
        return LockUtils.executeInLock(accessTreeLock, () -> widgetsByLayer.add(widget));
    }

    private void shiftUpperWidgets(Widget widget) {
        // iterator for subtree where all elements have greater index than given widget
        Iterator<Widget> subtreeIt = widgetsByLayer.tailSet(widget).iterator();

        Set<Widget> widgetsToShift = new HashSet<>();
        Widget previousWidget = widget;
        while (subtreeIt.hasNext()) {
            Widget currentWidget = subtreeIt.next();
            if (thereIsAGapToInsertAWidget(previousWidget, currentWidget)) {
                break;
            } else {
                widgetsToShift.add(currentWidget);
                subtreeIt.remove();
            }
            previousWidget = currentWidget;
        }

        // because we can not modify keys in tree collection - we remove that keys and add them again
        widgetsToShift.forEach(w -> w.setZIndex(w.getZIndex() + 1));
        widgetsByLayer.addAll(widgetsToShift);
    }

    private boolean thereIsAGapToInsertAWidget(Widget previousWidget, Widget currentWidget) {
        return currentWidget.getZIndex() - previousWidget.getZIndex() > 1;
    }

    private void doInsertToBackground(Widget widget) {
        widget.setZIndex(getBackgroundIndex());

        boolean inserted;
        do {
            inserted = tryInsertOnEmptyIndex(widget);

            if (!inserted) {
                widget.setZIndex(getBackgroundIndex());
            }
        } while (!inserted);
    }

    private int getBackgroundIndex() {
        return LockUtils.executeInLock(accessTreeLock,
                () -> widgetsByLayer.isEmpty() ? WidgetCache.DEFAULT_BACKGROUND_INDEX : widgetsByLayer.first()
                        .getZIndex() - 1);
    }

    public List<Widget> getAllWidgetsSortedByLayer() {
        return LockUtils.executeInLock(accessTreeLock, () -> new ArrayList<>(widgetsByLayer));
    }

    public void remove(Widget value) {
        LockUtils.executeInLock(accessTreeLock, () -> widgetsByLayer.remove(value));
    }

    public List<Widget> getAllWidgetsSortedByLayer(int pageNumber, int pageSize) {
        List<Widget> result = new ArrayList<>();
        int indexFromInc = pageNumber * pageSize;
        int indexToExc = indexFromInc + pageSize;

        LockUtils.executeInLock(accessTreeLock, () -> {
            Iterator<Widget> it = widgetsByLayer.iterator();

            int current = 0;
            while (it.hasNext()) {
                Widget element = it.next();
                if (current >= indexFromInc) {
                    result.add(element);
                }

                current++;
                if (current == indexToExc) {
                    break;
                }
            }
        });
        return result;
    }
}
