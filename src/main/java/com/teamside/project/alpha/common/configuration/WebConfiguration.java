package com.teamside.project.alpha.common.configuration;

import com.teamside.project.alpha.common.configuration.converter.StringToAuthTypeConverter;
import com.teamside.project.alpha.common.configuration.converter.StringToMyGroupTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToAuthTypeConverter());
        registry.addConverter(new StringToMyGroupTypeConverter());
    }
}
