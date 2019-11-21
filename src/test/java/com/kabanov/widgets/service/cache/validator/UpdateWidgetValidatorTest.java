package com.kabanov.widgets.service.cache.validator;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.xml.bind.ValidationException;

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
@WebMvcTest(value = UpdateWidgetValidator.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UpdateWidgetValidatorTest {

    private @Autowired UpdateWidgetValidator validator;
    
    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInvalidFieldsIsUpdated() throws ValidationException {

        Widget existing = WidgetUtils.createWidget(1);
        Widget updated = new Widget(existing);
        updated.setUuid(UUID.randomUUID());

        validator.validate(existing, updated);
    }

    @Test
    public void shouldNotThrowExceptionWhenValidFieldsIsUpdated() throws ValidationException {
        UUID uuid = UUID.randomUUID();
        Widget widget = new Widget(uuid, new Point(1, 1), 2, 3, 4, LocalDateTime.now());
        Widget updatedWidget = new Widget(uuid, new Point(2, 2), 3, 4, 5, LocalDateTime.now());

        validator.validate(widget, updatedWidget);
    }
}