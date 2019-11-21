package com.kabanov.widgets.service.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Component
public class WidgetLayersStorage {

    private static final int BACKGROUND_INDEX = 0;

    private ConcurrentSkipListSet<Widget> widgetsByLayer = new ConcurrentSkipListSet<>(
            Comparator.comparingInt(Widget::getZIndex));

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock updateTreeLock = readWriteLock.writeLock();
    private Lock accessTreeLock = readWriteLock.readLock();

    public Widget add(Widget widget) {
        if (isBackgroundWidget(widget)) {
            doInsertToBackground(widget);
        } else {
            doInsertWithShift(widget);
        }
        return widget;
    }

    private boolean isBackgroundWidget(Widget widget) {
        return widget.getZIndex() == null;
    }

    private void doInsertWithShift(Widget widget) {
        boolean inserted;

        inserted = tryInsertOnEmptyIndex(widget);
        if (!inserted) {
            updateTreeLock.lock();
            try {
                // try again inside lock. Maybe z index of conflicted element has been changed in another thread
                inserted = widgetsByLayer.add(widget);
                if (!inserted) {
                    shiftUpperWidgets(widget);
                    widgetsByLayer.add(widget);
                }
            } finally {
                updateTreeLock.unlock();
            }
        }
    }

    public void update(Widget oldWidget, Widget updatedWidget) {
        updateTreeLock.lock();
        try {
            widgetsByLayer.remove(oldWidget);
            doInsertWithShift(updatedWidget);
        } finally {
            updateTreeLock.unlock();
        }
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
        accessTreeLock.lock();
        try {
            return widgetsByLayer.add(widget);
        } finally {
            accessTreeLock.unlock();
        }
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
        accessTreeLock.lock();
        try {
            return widgetsByLayer.isEmpty() ? BACKGROUND_INDEX : widgetsByLayer.first().getZIndex() - 1;
        } finally {
            accessTreeLock.unlock();
        }
    }

    public List<Widget> getAllWidgetsSortedByLayer() {
        accessTreeLock.lock();
        try {
            return new ArrayList<>(widgetsByLayer);
        } finally {
            accessTreeLock.unlock();
        }
    }

    public void remove(Widget value) {
        accessTreeLock.lock();
        try {
            widgetsByLayer.remove(value);
        } finally {
            accessTreeLock.unlock();
        }
    }

    public static class Result {
        private final Widget resultedWidget;
        private final Collection<Widget> changedWidgets;

        public Result(Widget resultedWidget, Collection<Widget> changedWidgets) {
            this.resultedWidget = resultedWidget;
            this.changedWidgets = changedWidgets;
        }
    }
}
