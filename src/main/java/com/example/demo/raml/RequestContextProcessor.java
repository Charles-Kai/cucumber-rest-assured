package com.example.demo.raml;

import org.raml.v2.api.RamlModelResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class RequestContextProcessor extends AbstractMessageConverterMethodArgumentResolver {

    @Autowired(required = false)
    private transient RamlModelResult result;

    @Autowired
    private RamlV8validate ramlV8validate;

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

        String body = (String) readWithMessageConverters(nativeWebRequest, methodParameter, String.class);
        return ramlV8validate.validate(result.getApiV08(), nativeWebRequest, inputMessage, body, getClass(methodParameter));
    }

    public static Class<?> getClass(MethodParameter methodParameter) {
        Assert.isAssignable(RequestContext.class, methodParameter.getParameterType());
        Class<?> bodyClass = null;
        Type parameterType = methodParameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            if (parameterizedType.getActualTypeArguments().length ==1) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
        } else if (parameterType instanceof Class) {
            bodyClass = Object.class;
        }
        return Optional.ofNullable(bodyClass).orElseThrow(() -> new IllegalArgumentException("can not find parameter type"));
    }

}
