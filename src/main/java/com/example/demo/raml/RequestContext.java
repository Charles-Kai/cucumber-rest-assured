package com.example.demo.raml;

import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

public interface RequestContext<T> {
    MultiValueMap<String, String> getHeaders();

    Optional<String> getParamter(String key);

    Optional<String> getUrlPath();

    Optional<T> getBody();

    Class<?>  getBodyClass();

    HttpMethod getMethod();

    MultiValueMap<String, String> getQueryParameterMap();

    MultiValueMap<String, String> getUrlParameterMap();


}
