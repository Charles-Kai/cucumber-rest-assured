package com.example.demo.raml;

import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequestContext<T> implements RequestContext {
    private final MultiValueMap<String, String> headers;
    private final MultiValueMap<String, String> queryParameterMap;
    private final MultiValueMap<String, String> urlParameterMap;
    private final Optional<T> body;
    private final Class<?> bodyClass;
    private final Optional<String> urlPath;
    private final HttpMethod method;

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Optional<String> getParamter(String key) {
        String paramter = Optional.ofNullable(getByIgnoringKey(urlParameterMap, key)).orElse(getByIgnoringKey(queryParameterMap, key));
        return Optional.ofNullable(paramter);
    }

    @Override
    public Optional<String> getUrlPath() {
        return this.urlPath;
    }

    @Override
    public Optional<T> getBody() {
        return this.body;
    }

    @Override
    public Class<?> getBodyClass() {
        return this.bodyClass;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public MultiValueMap<String, String> getQueryParameterMap() {
        return this.queryParameterMap;
    }

    @Override
    public MultiValueMap<String, String> getUrlParameterMap() {
        return this.urlParameterMap;
    }

    private String getByIgnoringKey(MultiValueMap<String, String> multiValueMap, String key) {
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                if (entry.getValue().isEmpty()) {
                    return null;
                } else {
                    return entry.getValue().get(0);
                }
            }
        }
        return null;
    }


    public HttpRequestContext(MultiValueMap<String, String> headers, MultiValueMap<String, String> parameterValueMap, MultiValueMap<String, String> urlParamterMap, T body, Class<?> bodyClass, String urlPath, HttpMethod method) {
        this.headers = headers;
        this.queryParameterMap = parameterValueMap;
        this.urlParameterMap = urlParamterMap;
        this.body = Optional.ofNullable(body);
        this.bodyClass = bodyClass;
        this.urlPath = Optional.ofNullable(urlPath);
        this.method = method;
    }
}
