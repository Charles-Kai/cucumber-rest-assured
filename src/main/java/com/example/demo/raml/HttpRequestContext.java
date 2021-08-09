package com.example.demo.raml;

import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequestContext<T> implements RequestContext {
    private final MultiValueMap<String, String> headers;
    private final MultiValueMap<String, String> parameterValueMap;
    private final MultiValueMap<String, String> urlParameterMap;
    private final Optional<T> body;

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Optional<String> getParamter(String key) {
        String paramter = Optional.ofNullable(getByIgnoringKey(urlParameterMap, key)).orElse(getByIgnoringKey(parameterValueMap, key));
        return Optional.ofNullable(paramter);
    }

    @Override
    public Optional<T> getBody() {
        return Optional.empty();
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


    public HttpRequestContext(MultiValueMap<String, String> headers, MultiValueMap<String, String> parameterValueMap, MultiValueMap<String, String> urlParamterMap, T body) {
        this.headers = headers;
        this.parameterValueMap = parameterValueMap;
        this.urlParameterMap = urlParamterMap;
        this.body = Optional.ofNullable(body);
    }
}
