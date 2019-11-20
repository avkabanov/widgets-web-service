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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(value = WidgetLayersStorage.class)
public class WidgetLayersStorageTest {

    @Autowired private WidgetLayersStorage widgetLayersStorage;

    @Test
    public void addShouldReturnNewlyCreatedWidget() {
        Widget widget = createWidget(2);
        Widget expected = createWidget(widget, 2);

        Widget result = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void addShouldReturnNewlyCreatedBackgroundWidget() {
        Widget widget = createWidget(null);
        Widget expected = createWidget(widget, 0);

        Widget result = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldReturnWidgetInZOrderWhenAddedInRandomOrder() {
        Widget one = createWidget(1);
        Widget two = createWidget(2);
        Widget three = createWidget(3);
        Widget four = createWidget(4);

        List<Widget> expected = deepCopyAsList(one, two, three, four);

        widgetLayersStorage.add(three);
        widgetLayersStorage.add(two);
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(four);

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

        widgetLayersStorage.add(one);
        widgetLayersStorage.add(two);
        widgetLayersStorage.add(three);
        widgetLayersStorage.add(four);
        widgetLayersStorage.add(insertedInPlaceOfTwo);

        List<Widget> result = widgetLayersStorage.getAllWidgetsSortedByLayer();

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldMoveBGWidgetsToToBackgroundWhenBackgroundWidgetIsCreated() {
        Widget minusOne = createWidget(-1);
        Widget one = createWidget(1);
        Widget four = createWidget(4);

        Widget firstBg = createWidget(null);
        Widget secondBg = createWidget(null);

        List<Widget> expected = Arrays.asList(
                createWidget(secondBg, -3),
                createWidget(firstBg, -2),
                createWidget(minusOne, -1),
                createWidget(one, 1),
                createWidget(four, 4)
        );

        widgetLayersStorage.add(minusOne);
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(four);
        widgetLayersStorage.add(firstBg);
        widgetLayersStorage.add(secondBg);

        List<Widget> result = widgetLayersStorage.getAllWidgetsSortedByLayer();

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldBeInitialPositionZeroForBackgroundWidgets() {
        Widget widget = createWidget(null);
        List<Widget> expected = Arrays.asList(createWidget(widget, 0));

        widgetLayersStorage.add(widget);

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