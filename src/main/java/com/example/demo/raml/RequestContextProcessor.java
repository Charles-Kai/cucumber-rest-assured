package com.example.demo.raml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raml.v2.api.RamlModelResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class RequestContextProcessor extends AbstractMessageConverterMethodArgumentResolver {

    @Autowired(required = false)
    private transient RamlModelResult result;

    @Autowired
    private RamlV8validate ramlV8validate;

    @Autowired
    private ObjectMapper objectMapper;


    public RequestContextProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return HttpRequestContext.class == methodParameter.getParameterType() | RequestContext.class == methodParameter.getParameterType();
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        ServletServerHttpRequest inputMessage = createInputMessage(nativeWebRequest);
        HttpMethod httpMethod = inputMessage.getMethod();
        String endpointPathPattern = (String) nativeWebRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        MultiValueMap<String, String> headers = inputMessage.getHeaders();
        Map<String, String[]> queryParameterMap = inputMessage.getServletRequest().getParameterMap();
        MultiValueMap<String, String> parameterValueMap = MapUtil.convertArrayMapToParamMap(queryParameterMap);
        Map<String, String> urlParameterHashMap = (Map<String, String>) nativeWebRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        MultiValueMap<String, String> urlParameterMap = MapUtil.convertHashMapToParamMap(urlParameterHashMap);

        String body = (String) readWithMessageConverters(nativeWebRequest, methodParameter, String.class);
        Object responseBody = null;
        Class<?> bodyClass = getClass(methodParameter);
        if (HttpMethod.POST == httpMethod || HttpMethod.PUT == httpMethod) {
            if (bodyClass.isAssignableFrom(String.class)) {
                responseBody = body;
            } else {
                try {
                    responseBody = objectMapper.readValue(body, bodyClass);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        RequestContext<?> httpRequestContext = new HttpRequestContext(headers, parameterValueMap, urlParameterMap, responseBody,
                bodyClass, endpointPathPattern, httpMethod);
        ramlV8validate.validate(Objects.requireNonNull(result.getApiV08()), httpRequestContext, body);
        return httpRequestContext;
    }

    public static Class<?> getClass(MethodParameter methodParameter) {
        Assert.isAssignable(RequestContext.class, methodParameter.getParameterType());
        Class<?> bodyClass = null;
        Type parameterType = methodParameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
        } else if (parameterType instanceof Class) {
            bodyClass = Object.class;
        }
        return Optional.ofNullable(bodyClass).orElseThrow(() -> new IllegalArgumentException("can not find parameter type"));
    }

}
