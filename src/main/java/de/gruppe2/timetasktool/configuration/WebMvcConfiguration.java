package de.gruppe2.timetasktool.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //
        // Access static resources like JavaScript and CSS files.
        //

        //
        // /bootstrap/css/bootstrap.min.css
        // /bootstrap/js/bootstrap.min.js
        //
        registry.addResourceHandler("/bootstrap/**") //
                .addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap/4.1.3/");

        //
        // /material-design-icons/material-icons.css
        //
        registry.addResourceHandler("/material-design-icons/**") //
                .addResourceLocations("classpath:/META-INF/resources/webjars/material-design-icons/3.0.1/");

    }
}


