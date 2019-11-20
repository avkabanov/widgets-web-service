package com.kabanov.widgets.service.cache.validator;

import javax.xml.bind.ValidationException;

import org.springframework.stereotype.Component;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Component
public class UpdateWidgetValidator {

    public void validate(Widget oldWidget, Widget updatedWidget) throws ValidationException {
        if (!oldWidget.getUuid().equals(updatedWidget.getUuid())) {
            throw new ValidationException(
                    "UUID can not be modified. Existing widget: " + oldWidget + " Modify request: " + updatedWidget);
        }

    }
}
