package com.kabanov.widgets.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.kabanov.widgets.dao.db.DatabaseWidgetCache;

/**
 * @author Kabanov Alexey
 */
@Profile("databaseStorage")
@Import(DatabaseWidgetCache.class)
@Configuration
public class DatabaseStorageConfig {

}
