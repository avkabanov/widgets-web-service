package com.kabanov.widgets.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kabanov.widgets.controller.request.FilterRequest;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.WidgetService;

/**
 * @author Kabanov Alexey
 */
@RestController
@RequestMapping(path = "/filter")
public class FilterController {
    
    @Autowired WidgetService widgetService;

    @GetMapping(path = "/inBounds")
    @ResponseBody
    public ResponseEntity<List<Widget>> getAllWidgetsSortedByLayer(@Valid
                                                                   @RequestBody FilterRequest filterRequest) {
        List<Widget> result = widgetService.getAllWidgetsInBound(filterRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
