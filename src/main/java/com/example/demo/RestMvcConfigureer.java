package com.example.demo;

import com.example.demo.raml.RequestContextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class RestMvcConfigureer implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RestMvcConfigureer.class);
    @Autowired
    private transient RequestContextProcessor requestContextProcessor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        log.info("add RequestContextProcessor handlerMethodArgumentResolver");
        resolvers.add(requestContextProcessor);
    }


    @Bean
    public RequestContextProcessor requestContextProcessor(List<HttpMessageConverter<?>> converters) {
        log.info("create RequestContextProcessor bean");
        return new RequestContextProcessor(converters);
    }

}
