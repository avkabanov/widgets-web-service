package com.kabanov.widgets.controller;

import java.awt.*;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabanov.widgets.controller.request.CreateWidgetRequest;
import com.kabanov.widgets.controller.request.FilterRequest;
import com.kabanov.widgets.controller.request.UpdateWidgetRequest;
import com.kabanov.widgets.domain.Widget;
import com.kabanov.widgets.service.widget.WidgetService;
import com.kabanov.widgets.test_utils.WidgetTestUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kabanov Alexey
 */
@ActiveProfiles("inMemoryStorage")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ImportResource("classpath:spring-config.xml")
public class WidgetControllerItTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private WidgetService widgetService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    public void shouldCreateWidgetWhenCreateRequestReceived() throws Exception {
        CreateWidgetRequest createWidgetRequest = new CreateWidgetRequest(
                new Point(0, 0), 50, 100, 1
        );
        String content = objectMapper.writeValueAsString(createWidgetRequest);

        MockHttpServletRequestBuilder request = post("/widget/create")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    objectMapper.readValue(response, Widget.class);
                });
    }

    @Test
    public void shouldGetWidgetByIdWhenReceivedRequestToGet() throws Exception {
        Widget widget = WidgetTestUtils.createWidget(1);
        widgetService.addWidgetToCache(widget);

        MockHttpServletRequestBuilder request = get("/widget/" + widget.getUuid());

        mockMvc
                .perform(request)
                .andExpect(status().isFound())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    objectMapper.readValue(content, Widget.class);
                });
    }

    @Test
    public void shouldGetAllWidgetByIdWhenReceivedRequestToGet() throws Exception {
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(1));
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(2));
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(3));

        MockHttpServletRequestBuilder request = get("/widget/all");

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    objectMapper.readValue(content, new TypeReference<ArrayList<Widget>>() {
                    });
                });
    }

    @Test
    public void shouldWidgetBeUpdatedWhenUpdateIsCalled() throws Exception {
        Widget widget = WidgetTestUtils.createWidget(1);
        widgetService.addWidgetToCache(widget);

        UpdateWidgetRequest updateWidgetRequest = new UpdateWidgetRequest();
        updateWidgetRequest.setUuid(widget.getUuid());
        updateWidgetRequest.setWidth(widget.getWidth() + 10);
        updateWidgetRequest.setzIndex(1);
        updateWidgetRequest.setHeight(1);
        updateWidgetRequest.setStartPoint(new Point(1, 1));

        MockHttpServletRequestBuilder request = patch("/widget/update")
                .content(objectMapper.writeValueAsString(updateWidgetRequest))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    objectMapper.readValue(response, Widget.class);
                });

    }

    @Test
    public void shouldGetAllWidgetWithinTheBoundWhenFilterMethodIsCalled() throws Exception {
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(new Point(0, 0), 50, 50, 50));
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(new Point(150, 150), 100, 100));

        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setStartPoint(new Point(0, 0));
        filterRequest.setHeight(100);
        filterRequest.setWidth(100);

        MockHttpServletRequestBuilder request = get("/widget/filter")
                .content(objectMapper.writeValueAsString(filterRequest))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    objectMapper.readValue(content, new TypeReference<ArrayList<Widget>>() {
                    });
                });
    }

    @Test
    public void shouldRemoveWidgetWhenRemoveItCalled() throws Exception {
        Widget widgetToRemove = WidgetTestUtils.createWidget(new Point(0, 0), 50, 50, 50);
        widgetService.addWidgetToCache(widgetToRemove);
        widgetService.addWidgetToCache(WidgetTestUtils.createWidget(new Point(150, 150), 100, 100));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(
                "/widget/delete/" + widgetToRemove.getUuid())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isOk());
    }

}