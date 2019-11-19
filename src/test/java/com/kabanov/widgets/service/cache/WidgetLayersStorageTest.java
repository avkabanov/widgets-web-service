package com.kabanov.widgets.service.cache;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = WidgetLayersStorage.class)
public class WidgetLayersStorageTest {

    @Autowired private WidgetLayersStorage widgetLayersStorage;

    @Test
    public void shouldReturnWidgetInZOrderWhenAddedInRandomOrder() {
        Widget one = createWidget(1);
        Widget two = createWidget(2);
        Widget three = createWidget(3);
        Widget four = createWidget(4);

        List<Widget> expected = deepCopyAsList(one, two, three, four);

        widgetLayersStorage.add(three, false);
        widgetLayersStorage.add(two, false);
        widgetLayersStorage.add(one, false);
        widgetLayersStorage.add(four, false);

        List<Widget> result = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldShiftAllNextWidgetsWhenWidgetWithSameZIndexIsAdded() {
        Widget one = createWidget(1);
        Widget two = createWidget(2);
        Widget three = createWidget(3);
        Widget four = createWidget(4);

        Widget insertedInPlaceOfTwo = createWidget(2);

        List<Widget> expected = Arrays.asList(
                createWidget(one, 1),
                createWidget(insertedInPlaceOfTwo, 2),
                createWidget(two, 3),
                createWidget(three, 4),
                createWidget(four, 5)
        );

        widgetLayersStorage.add(one, false);
        widgetLayersStorage.add(two, false);
        widgetLayersStorage.add(three, false);
        widgetLayersStorage.add(four, false);
        widgetLayersStorage.add(insertedInPlaceOfTwo, false);

        List<Widget> result = widgetLayersStorage.getAllWidgetsSortedByLayer();

        Assert.assertEquals(expected, result);
    }

    private Widget createWidget(Widget widget, Integer zIndex) {
        Widget result = new Widget(widget);
        result.setZIndex(zIndex);
        return result;
    }

    private Widget createWidget(Integer zIndex) {
        return new Widget(UUID.randomUUID(), new Point(0, 0), 1, 1, zIndex, LocalDateTime.now());
    }

    private List<Widget> deepCopyAsList(Widget... widgets) {
        return Arrays.stream(widgets).map(Widget::new).collect(Collectors.toList());
    }

}