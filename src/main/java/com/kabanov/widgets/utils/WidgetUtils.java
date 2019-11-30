package com.kabanov.widgets.utils;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
public class WidgetUtils {

    private WidgetUtils() {
    }

    public static boolean isBackgroundWidget(Widget widget) {
        return widget.getZIndex() == null;
    }
}
