package com.example.demo.raml;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class MapUtil {
    public static MultiValueMap<String, String> convertHashMapToParamMap(Map<String, String> urlParamterHashMap) {
        Map<String, String> sourceMap = Optional.ofNullable(urlParamterHashMap).orElse(new HashMap<>());
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(sourceMap.size());
        for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
            multiValueMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        return multiValueMap;
    }

    public static MultiValueMap<String, String> convertArrayMapToParamMap(Map<String, String[]> parameterMap) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            multiValueMap.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        return multiValueMap;
    }

    public static MultiValueMap<String, String> convertSingleMapToParamMap(Map<String, List<String>> singleMap) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(singleMap.size());
        for (Map.Entry<String, List<String>> entry : singleMap.entrySet()) {
            multiValueMap.put(entry.getKey(), entry.getValue());
        }
        return multiValueMap;
    }
}
