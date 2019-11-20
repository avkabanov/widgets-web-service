package com.kabanov.widgets.service.cache;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.cache.validator.UpdateWidgetValidator;
import com.kabanov.widgets.test_utils.WidgetUtils;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = {WidgetLayersStorage.class, UpdateWidgetValidator.class})
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
    public void shouldUpdateWidgetWhenUpdateIsCalled() throws ValidationException {
        UUID uuid = UUID.randomUUID();
        Widget widget = new Widget(uuid, new Point(1, 1), 2, 3, 4, LocalDateTime.now());
        Widget updatedWidget = new Widget(uuid, new Point(2, 2), 3, 4, 5, LocalDateTime.now());

        Widget expected = new Widget(updatedWidget);

        widgetCache.add(widget);
        Widget actual = widgetCache.updateWidget(uuid, updatedWidget);
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(expected, widgetCache.getWidget(uuid));
        Assert.assertEquals(expected, widgetCache.getAllWidgetsSortedByLayer().get(0));
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenIdIsModified() throws ValidationException {
        Widget widget = WidgetUtils.createWidget(1);
        Widget updatedWidget = new Widget(widget);
        updatedWidget.setUuid(UUID.randomUUID());

        widgetCache.add(widget);
        widgetCache.updateWidget(widget.getUuid(), updatedWidget);
    }

}