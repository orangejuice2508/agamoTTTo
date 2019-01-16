package de.gruppe2.agamoTTTo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is used for customizing the basic Spring MVC configuration.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    /*
     Access static resources like JavaScript and CSS files by "mapping" them to a permanent directory.
    */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathToWebjars = "classpath:/META-INF/resources/webjars";

        /*
         /bootstrap/css/bootstrap.min.css
         /bootstrap/js/bootstrap.min.js
        */
        registry.addResourceHandler("/bootstrap/**") //
                .addResourceLocations(pathToWebjars + "/bootstrap/4.1.3/");

        /*
         /font-awesome/css/all.min.css
        */
        registry.addResourceHandler( "/font-awesome/**") //
                .addResourceLocations(pathToWebjars + "/font-awesome/5.5.0/");

        /*
         /jquery/jquery.min.js
        */
        registry.addResourceHandler("/jquery/**") //
                .addResourceLocations(pathToWebjars + "/jquery/3.3.1-1/");
    }
}


