package com.example.demo.raml;

import org.springframework.util.MultiValueMap;

import java.util.Optional;

public interface RequestContext<T> {
    MultiValueMap<String, String> getHeaders();

    Optional<String> getParamter(String key);

    Optional<T> getBody();
}
