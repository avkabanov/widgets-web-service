package com.kabanov.widgets.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kabanov.widgets.controller.request.CreateWidgetRequest;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.WidgetService;

@RestController
@RequestMapping(path = "/widget")
public class WidgetController {
    private WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping(path = "/create")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Widget> createWidget(@RequestBody
                                               @Valid CreateWidgetRequest createWidgetRequest) {
        Widget widget = widgetService.createWidget(createWidgetRequest);
        return new ResponseEntity<>(widget, HttpStatus.CREATED);
    }

    @GetMapping(path = "/id/{uuid}")
    @ResponseBody
    public ResponseEntity<Widget> getWidget(@NotNull(message = "UUID can not be null")
                                            @PathVariable UUID uuid) {
        Widget result = widgetService.getWidget(uuid);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.FOUND);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found id: " + uuid);
        }
    }

    @GetMapping(path = "/all")
    @ResponseBody
    public ResponseEntity<List<Widget>> getAllWidgetsSortedByLayer() {
        List<Widget> result = widgetService.getAllWidgetsSortedByLayer();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(path = "/update")
    @ResponseBody
    public ResponseEntity<Widget> updateWidget(@Valid
                                               @RequestBody UpdateWidgetRequest updateRequest) {
        try {
            Widget result = widgetService.updateWidget(updateRequest);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Update widget error: " + e.getLocalizedMessage());
        }
    }
}
