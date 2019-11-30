package com.kabanov.widgets.test_utils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
public class WidgetTestUtils {

    private WidgetTestUtils() {
    }

    public static Widget createWidget(Widget widget, Integer zIndex) {
        Widget result = new Widget(widget);
        result.setZIndex(zIndex);
        return result;
    }

    public static Widget createWidget(Integer zIndex) {
        return new Widget(UUID.randomUUID(), new Point(0, 0), 1, 1, zIndex, LocalDateTime.now());
    }

    public static Widget createWidget(Point startPoint, int height, int width, Integer zIndex) {
        return new Widget(UUID.randomUUID(), startPoint, height, width, zIndex, LocalDateTime.now());
    }

    public static Widget createWidget(Point startPoint, int height, int width) {
        return new Widget(UUID.randomUUID(), startPoint, height, width, null, LocalDateTime.now());
    }

    public static List<Widget> deepCopyToList(Widget... widgets) {
        return Arrays.stream(widgets).map(Widget::new).collect(Collectors.toList());
    }

}
