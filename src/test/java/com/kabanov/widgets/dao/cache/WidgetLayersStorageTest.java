package com.kabanov.widgets.dao.cache;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.test_utils.WidgetTestUtils;

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
        Widget widget = WidgetTestUtils.createWidget(2);
        Widget expected = WidgetTestUtils.createWidget(widget, 2);

        Widget actual = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addShouldReturnNewlyCreatedBackgroundWidget() {
        Widget widget = WidgetTestUtils.createWidget(null);
        Widget expected = WidgetTestUtils.createWidget(widget, 0);

        Widget actual = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnWidgetInZOrderWhenAddedInRandomOrder() {
        Widget one = WidgetTestUtils.createWidget(1);
        Widget two = WidgetTestUtils.createWidget(2);
        Widget three = WidgetTestUtils.createWidget(3);
        Widget four = WidgetTestUtils.createWidget(4);

        List<Widget> expected = WidgetTestUtils.deepCopyToList(one, two, three, four);

        widgetLayersStorage.add(three);
        widgetLayersStorage.add(two);
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(four);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldShiftAllNextWidgetsWhenWidgetWithSameZIndexIsAdded() {
        Widget one = WidgetTestUtils.createWidget(1);
        Widget two = WidgetTestUtils.createWidget(2);
        Widget three = WidgetTestUtils.createWidget(3);
        Widget four = WidgetTestUtils.createWidget(4);

        Widget insertedInPlaceOfTwo = WidgetTestUtils.createWidget(2);

        List<Widget> expected = Arrays.asList(
                WidgetTestUtils.createWidget(one, 1),
                WidgetTestUtils.createWidget(insertedInPlaceOfTwo, 2),
                WidgetTestUtils.createWidget(two, 3),
                WidgetTestUtils.createWidget(three, 4),
                WidgetTestUtils.createWidget(four, 5)
        );

        widgetLayersStorage.add(one);
        widgetLayersStorage.add(two);
        widgetLayersStorage.add(three);
        widgetLayersStorage.add(four);
        widgetLayersStorage.add(insertedInPlaceOfTwo);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldMoveBGWidgetsToToBackgroundWhenBackgroundWidgetIsCreated() {
        Widget minusOne = WidgetTestUtils.createWidget(-1);
        Widget one = WidgetTestUtils.createWidget(1);
        Widget four = WidgetTestUtils.createWidget(4);

        Widget firstBg = WidgetTestUtils.createWidget(null);
        Widget secondBg = WidgetTestUtils.createWidget(null);

        List<Widget> expected = Arrays.asList(
                WidgetTestUtils.createWidget(secondBg, -3),
                WidgetTestUtils.createWidget(firstBg, -2),
                WidgetTestUtils.createWidget(minusOne, -1),
                WidgetTestUtils.createWidget(one, 1),
                WidgetTestUtils.createWidget(four, 4)
        );

        widgetLayersStorage.add(minusOne);
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(four);
        widgetLayersStorage.add(firstBg);
        widgetLayersStorage.add(secondBg);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldWidgetBeRemovedWhenRemoveIsCalled() {
        Widget one = WidgetTestUtils.createWidget(1);
        Widget two = WidgetTestUtils.createWidget(2);
        
        List<Widget> expected = Arrays.asList(two);
        
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(two);
        widgetLayersStorage.remove(one);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldBeInitialPositionZeroForBackgroundWidgets() {
        Widget widget = WidgetTestUtils.createWidget(null);
        List<Widget> expected = Arrays.asList(WidgetTestUtils.createWidget(widget, 0));

        widgetLayersStorage.add(widget);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

}