package com.kabanov.widgets.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.kabanov.widgets.dao.cache.InMemoryWidgetCache;

/**
 * @author Kabanov Alexey
 */
@Profile("inMemoryStorage")
@Import(InMemoryWidgetCache.class)
@Configuration
public class InMemoryStorageConfig {

}
