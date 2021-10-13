package com.hpe.mgr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ApiConfig extends WebMvcConfigurerAdapter {

    /**
     * Prevent '.' from truncating the last path parameter
     * Since clusters names contain '.'s Spring must not chop off the final part.
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }
}
