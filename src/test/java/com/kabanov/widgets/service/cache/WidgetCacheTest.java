package com.kabanov.widgets.service.cache;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.test_utils.WidgetUtils;

import static com.kabanov.widgets.test_utils.TimeUtils.sleepMillis;
import static com.kabanov.widgets.test_utils.WidgetUtils.createWidget;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = {WidgetLayersStorage.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WidgetCacheTest {

    @Autowired private WidgetCache widgetCache;

    @Test
    public void addShouldReturnNewlyCreatedWidget() {
        Widget widget = createWidget(2);
        Widget expected = createWidget(widget, 2);

        Widget actual = widgetCache.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addShouldReturnNewlyCreatedBackgroundWidget() {
        Widget widget = createWidget(null);
        Widget expected = createWidget(widget, 0);

        Widget actual = widgetCache.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnFullInformationOnWidgetWhenIdIsGiven() {
        Widget widget = createWidget(2);
        Widget expected = createWidget(widget, 2);

        widgetCache.add(widget);
        Widget actual = widgetCache.getWidget(widget.getUuid());

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAddWidgetWithGivenWidgetIdThatAlreadyExist() {
        Widget first = createWidget(2);
        Widget second = createWidget(2);
        second.setUuid(first.getUuid());

        widgetCache.add(first);
        widgetCache.add(second);
    }

    @Test
    public void shouldReturnNullWhenCacheIsEmpty() {
        Widget actual = widgetCache.getWidget(UUID.randomUUID());
        Assert.assertNull(actual);
    }

    @Test
    public void shouldReturnWidgetsInSortedOrder() {
        Widget one = createWidget(1);
        Widget two = createWidget(2);
        Widget three = createWidget(3);
        Widget four = createWidget(4);

        List<Widget> expected = WidgetUtils.deepCopyToList(one, two, three, four);

        widgetCache.add(three);
        widgetCache.add(two);
        widgetCache.add(one);
        widgetCache.add(four);

        List<Widget> actual = widgetCache.getAllWidgetsSortedByLayer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldUpdateWidgetWhenUpdateIsInvoked() {
        UUID uuid = UUID.randomUUID();
        Widget widget = new Widget(uuid, new Point(1, 1), 2, 3, 4, LocalDateTime.now());

        UpdateWidgetRequest updateWidgetRequest = new UpdateWidgetRequest();
        updateWidgetRequest.setUuid(uuid);
        updateWidgetRequest.setStartPoint(new Point(2, 2));
        updateWidgetRequest.setHeight(3);
        updateWidgetRequest.setWidth(4);
        updateWidgetRequest.setzIndex(5);

        Widget expected = new Widget(uuid, new Point(2, 2), 3, 4, 5, LocalDateTime.now());
        widgetCache.add(widget);

        // sleep minimum amount of time in order to change last modification time later
        sleepMillis(1);
        Widget actual = widgetCache.updateWidget(updateWidgetRequest);
        
        // first of all we check last modification time
        Assert.assertTrue(actual.getLastModificationTime().isAfter(widget.getLastModificationTime()));
        
        // after we checked it, we set actual last modification time to expected widget in order to compare 
        // widget with #equals
        expected.setLastModificationTime(actual.getLastModificationTime());
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(expected, widgetCache.getWidget(uuid));
        Assert.assertEquals(expected, widgetCache.getAllWidgetsSortedByLayer().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithUpdatedWidgetNotFound() {
        Widget widget = createWidget(1);
        UpdateWidgetRequest updateRequest = new UpdateWidgetRequest();
        updateRequest.setUuid(UUID.randomUUID());
        updateRequest.setHeight(10); // update random field

        widgetCache.add(widget);
        widgetCache.updateWidget(updateRequest);
    }

    @Test
    public void shouldRemoveWidgetWhenRemoveIsInvoked() {
        Widget one = createWidget(1);
        Widget two = createWidget(2);

        List<Widget> expected = Arrays.asList(one);

        widgetCache.add(one);
        widgetCache.add(two);

        widgetCache.removeWidget(two.getUuid());

        Assert.assertNull(widgetCache.getWidget(two.getUuid()));
        Assert.assertEquals(expected, widgetCache.getAllWidgetsSortedByLayer());
    }

    @Test
    public void shouldReturnAllWidgetsInBound() {
        Widget first = createWidget(new Point(0, 0), 100, 100, 1);
        Widget second = createWidget(new Point(0, 50), 100, 100, 2);
        Widget third = createWidget(new Point(50, 50), 100, 100, 3);
        Bound bound = new Bound(new Point(0, 0), 150, 100);

        java.util.List<Widget> expected = WidgetUtils.deepCopyToList(first, second);

        widgetCache.add(first);
        widgetCache.add(second);
        widgetCache.add(third);
        List<Widget> actual = widgetCache.getAllWidgetsInBound(bound);

        Assert.assertEquals(expected, actual);
    }
}