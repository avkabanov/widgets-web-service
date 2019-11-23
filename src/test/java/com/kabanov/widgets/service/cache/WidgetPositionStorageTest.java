package com.kabanov.widgets.service.cache;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.domain.Bound;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.test_utils.WidgetUtils;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(value = WidgetPositionStorage.class)
public class WidgetPositionStorageTest {

    @Autowired WidgetPositionStorage positionStorage;

    @Test
    public void shouldReturnOnlyWidgetsInBound() {
        Widget first = WidgetUtils.createWidget(new Point(0, 0), 100, 100);
        Widget second = WidgetUtils.createWidget(new Point(0, 50), 100, 100);
        Widget third = WidgetUtils.createWidget(new Point(50, 50), 100, 100);
        Bound bound = new Bound(new Point(0, 0), 150, 100);

        java.util.List<Widget> expected = WidgetUtils.deepCopyToList(first, second);

        positionStorage.add(first);
        positionStorage.add(second);
        positionStorage.add(third);
        List<Widget> actual = positionStorage.getWidgetsInBound(bound);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnUpdatedWidgetsWhenWidgetIsUpdated() {
        Widget widget = WidgetUtils.createWidget(new Point(0, 0), 100, 100);  
        Widget updated = new Widget(widget);
        updated.setStartPoint(new Point(100, 100));

        Bound bound = new Bound(new Point(0, 0), 100, 100);
        
        positionStorage.add(widget);
        Assert.assertEquals(widget.getUuid(), positionStorage.getWidgetsInBound(bound).get(0).getUuid());

        positionStorage.update(widget, updated);
        Assert.assertEquals(Collections.emptyList(), positionStorage.getWidgetsInBound(bound));
    }

    @Test
    public void shouldNotReturnWidgetWhenWidgetIsRemoved() {
        Widget widget = WidgetUtils.createWidget(new Point(0, 0), 100, 100);

        Bound bound = new Bound(new Point(0, 0), 100, 100);

        positionStorage.add(widget);
        Assert.assertEquals(widget.getUuid(), positionStorage.getWidgetsInBound(bound).get(0).getUuid());

        positionStorage.remove(widget);
        Assert.assertEquals(Collections.emptyList(), positionStorage.getWidgetsInBound(bound));   
    }
}