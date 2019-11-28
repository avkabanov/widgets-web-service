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
import com.kabanov.widgets.test_utils.WidgetUtils;

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
        Widget widget = WidgetUtils.createWidget(2);
        Widget expected = WidgetUtils.createWidget(widget, 2);

        Widget actual = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addShouldReturnNewlyCreatedBackgroundWidget() {
        Widget widget = WidgetUtils.createWidget(null);
        Widget expected = WidgetUtils.createWidget(widget, 0);

        Widget actual = widgetLayersStorage.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnWidgetInZOrderWhenAddedInRandomOrder() {
        Widget one = WidgetUtils.createWidget(1);
        Widget two = WidgetUtils.createWidget(2);
        Widget three = WidgetUtils.createWidget(3);
        Widget four = WidgetUtils.createWidget(4);

        List<Widget> expected = WidgetUtils.deepCopyToList(one, two, three, four);

        widgetLayersStorage.add(three);
        widgetLayersStorage.add(two);
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(four);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldShiftAllNextWidgetsWhenWidgetWithSameZIndexIsAdded() {
        Widget one = WidgetUtils.createWidget(1);
        Widget two = WidgetUtils.createWidget(2);
        Widget three = WidgetUtils.createWidget(3);
        Widget four = WidgetUtils.createWidget(4);

        Widget insertedInPlaceOfTwo = WidgetUtils.createWidget(2);

        List<Widget> expected = Arrays.asList(
                WidgetUtils.createWidget(one, 1),
                WidgetUtils.createWidget(insertedInPlaceOfTwo, 2),
                WidgetUtils.createWidget(two, 3),
                WidgetUtils.createWidget(three, 4),
                WidgetUtils.createWidget(four, 5)
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
        Widget minusOne = WidgetUtils.createWidget(-1);
        Widget one = WidgetUtils.createWidget(1);
        Widget four = WidgetUtils.createWidget(4);

        Widget firstBg = WidgetUtils.createWidget(null);
        Widget secondBg = WidgetUtils.createWidget(null);

        List<Widget> expected = Arrays.asList(
                WidgetUtils.createWidget(secondBg, -3),
                WidgetUtils.createWidget(firstBg, -2),
                WidgetUtils.createWidget(minusOne, -1),
                WidgetUtils.createWidget(one, 1),
                WidgetUtils.createWidget(four, 4)
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
        Widget one = WidgetUtils.createWidget(1);
        Widget two = WidgetUtils.createWidget(2);
        
        List<Widget> expected = Arrays.asList(two);
        
        widgetLayersStorage.add(one);
        widgetLayersStorage.add(two);
        widgetLayersStorage.remove(one);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldBeInitialPositionZeroForBackgroundWidgets() {
        Widget widget = WidgetUtils.createWidget(null);
        List<Widget> expected = Arrays.asList(WidgetUtils.createWidget(widget, 0));

        widgetLayersStorage.add(widget);

        List<Widget> actual = widgetLayersStorage.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

}