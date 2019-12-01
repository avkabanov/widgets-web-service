package com.kabanov.widgets.service.widget;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.kabanov.widgets.dao.WidgetCache;
import com.kabanov.widgets.domain.PaginationProperties;

/**
 * @author Kabanov Alexey
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = WidgetService.class)
@AutoConfigureMockMvc
public class WidgetServiceTest {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 300;
    
    @MockBean private WidgetCache widgetCache;
    @MockBean private PaginationProperties paginationProperties;
    @Captor ArgumentCaptor<Integer> intCaptor;
    
    @Autowired private WidgetService widgetService;
    
    @Before
    public void setup() {
        Mockito.when(paginationProperties.getDefaultPageSize()).thenReturn(DEFAULT_PAGE_SIZE);
        Mockito.when(paginationProperties.getMaxPageSize()).thenReturn(MAX_PAGE_SIZE);
    }
    
    @Test
    public void shouldCallPaginationWithDefaultSizeWhenNoPageSizeGiven() {
        widgetService.getAllWidgetsSortedByLayer(10, null);
        
        Mockito.verify(widgetCache).getAllWidgetsSortedByLayer(Mockito.anyInt(), intCaptor.capture());
        Assert.assertEquals(DEFAULT_PAGE_SIZE, (int)intCaptor.getAllValues().get(0));
    }

    @Test
    public void shouldCallPaginationWithMaxSizeWhenPageSizeIsGreaterThanMax() {
        widgetService.getAllWidgetsSortedByLayer(10, MAX_PAGE_SIZE + 1);

        Mockito.verify(widgetCache).getAllWidgetsSortedByLayer(Mockito.anyInt(), intCaptor.capture());
        Assert.assertEquals(MAX_PAGE_SIZE, (int)intCaptor.getAllValues().get(0));
    }

    @Test
    public void shouldCallPaginationWithGivenArgumentsWhenArgumentsDoesNotViolateTheRules() {
        widgetService.getAllWidgetsSortedByLayer(10, 5);

        Mockito.verify(widgetCache).getAllWidgetsSortedByLayer(intCaptor.capture(), intCaptor.capture());
        Assert.assertEquals(10, (int)intCaptor.getAllValues().get(0));
        Assert.assertEquals(5, (int)intCaptor.getAllValues().get(1));
    }
}