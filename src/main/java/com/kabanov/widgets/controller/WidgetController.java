package com.kabanov.widgets.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kabanov.widgets.controller.create_widget.CreateWidgetRequest;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.WidgetService;

@RestController
@RequestMapping(path = "/widget")
public class WidgetController {
    @Autowired WidgetService widgetService;

    @PostMapping(path = "/createWidget")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Widget createWidget(@RequestBody @Valid CreateWidgetRequest createWidgetRequest, HttpServletRequest rq) {
        return widgetService.createWidget(createWidgetRequest);
    }
}
