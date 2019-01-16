package de.gruppe2.agamoTTTo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * This class is used to set up the database configuration.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorAware")
public class PersistenceConfiguration {

}
