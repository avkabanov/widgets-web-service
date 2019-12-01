package com.kabanov.widgets.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Kabanov Alexey
 */
@Component
public class PaginationProperties {
    private int defaultPageSize;
    private int maxPageSizec;

    @Autowired
    public PaginationProperties(@Value("${pagination.size.default}") int defaultPageSize,
                                @Value("${pagination.size.max}") int maxPageSizec) {
        this.defaultPageSize = defaultPageSize;
        this.maxPageSizec = maxPageSizec;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public int getMaxPageSize() {
        return maxPageSizec;
    }
}
