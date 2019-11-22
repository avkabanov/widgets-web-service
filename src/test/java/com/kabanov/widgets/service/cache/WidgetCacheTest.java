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
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.test_utils.WidgetUtils;

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
        Widget widget = WidgetUtils.createWidget(2);
        Widget expected = WidgetUtils.createWidget(widget, 2);

        Widget actual = widgetCache.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addShouldReturnNewlyCreatedBackgroundWidget() {
        Widget widget = WidgetUtils.createWidget(null);
        Widget expected = WidgetUtils.createWidget(widget, 0);

        Widget actual = widgetCache.add(widget);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnFullInformationOnWidgetWhenIdIsGiven() {
        Widget widget = WidgetUtils.createWidget(2);
        Widget expected = WidgetUtils.createWidget(widget, 2);

        widgetCache.add(widget);
        Widget actual = widgetCache.getWidget(widget.getUuid());

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAddWidgetWithGivenWidgetIdThatAlreadyExist() {
        Widget first = WidgetUtils.createWidget(2);
        Widget second = WidgetUtils.createWidget(2);
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
        Widget one = WidgetUtils.createWidget(1);
        Widget two = WidgetUtils.createWidget(2);
        Widget three = WidgetUtils.createWidget(3);
        Widget four = WidgetUtils.createWidget(4);

        List<Widget> expected = WidgetUtils.deepCopyAsList(one, two, three, four);

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
        updateWidgetRequest.setStartPoint (new Point(2, 2));
        updateWidgetRequest.setHeight(3);
        updateWidgetRequest.setWidth(4);
        updateWidgetRequest.setzIndex(5);

        Widget expected = new Widget(uuid, new Point(2, 2), 3, 4, 5, LocalDateTime.now());

        widgetCache.add(widget);
        Widget actual = widgetCache.updateWidget(updateWidgetRequest);
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(expected, widgetCache.getWidget(uuid));
        Assert.assertEquals(expected, widgetCache.getAllWidgetsSortedByLayer().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithUpdatedWidgetNotFound() {
        Widget widget = WidgetUtils.createWidget(1);
        UpdateWidgetRequest updateRequest = new UpdateWidgetRequest();
        updateRequest.setUuid(UUID.randomUUID());
        updateRequest.setHeight(10); // update random field

        widgetCache.add(widget);
        widgetCache.updateWidget(updateRequest);
    }

    @Test
    public void shouldRemoveWidgetWhenRemoveIsInvoked() {
        Widget one = WidgetUtils.createWidget(1);
        Widget two = WidgetUtils.createWidget(2);

        List<Widget> expected = Arrays.asList(one); 
        
        widgetCache.add(one);
        widgetCache.add(two);
        
        widgetCache.removeWidget(two.getUuid());

        Assert.assertNull(widgetCache.getWidget(two.getUuid()));
        Assert.assertEquals(expected, widgetCache.getAllWidgetsSortedByLayer());
    }
}