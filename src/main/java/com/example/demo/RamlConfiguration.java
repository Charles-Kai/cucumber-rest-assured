package com.example.demo;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "http.raml", name = "file")
@Configuration
public class RamlConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RamlConfiguration.class);

    @Bean
    public RamlModelResult ramlParser(final @Value("${http.raml.file}") String file) {
        RamlModelResult result = new RamlModelBuilder().buildApi(file);
        if (result.hasErrors()) {
            for (ValidationResult validationResult : result.getValidationResults()) {
                log.warn("message:{}, path{}", validationResult.getMessage(), validationResult.getPath());
            }
            throw new IllegalArgumentException("Error in creating the RAML model");
        } else if (result.isVersion08()) {
            log.info("RAML is Version08");
            return result;
        } else {
            throw new IllegalArgumentException("不支持1.0版本！");
        }
    }


}
