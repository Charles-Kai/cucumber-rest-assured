package com.example.demo.raml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.JSONBody;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.parameters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class RamlV8validate {

    private static final Logger log = LoggerFactory.getLogger(RamlV8validate.class);

    @Value("${http.raml.base-path}")
    private String basePath;

    @Autowired
    private ObjectMapper objectMapper;

    public HttpRequestContext validate(Api api, NativeWebRequest nativeWebRequest, ServletServerHttpRequest inputMessage, String body, Class<?> bodyClass) {
        HttpMethod httpMethod = inputMessage.getMethod();
        String endpointPathPattern = (String) nativeWebRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        MultiValueMap<String, String> headers = inputMessage.getHeaders();
        MultiValueMap<String, String> valueMap = MapUtil.convertSingleMapToParamMap(headers);
        Map<String, String[]> parameterMap = inputMessage.getServletRequest().getParameterMap();
        MultiValueMap<String, String> parameterValueMap = MapUtil.convertArrayMapToParamMap(parameterMap);
        Map<String, String> urlParameterHashMap = (Map<String, String>) nativeWebRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        MultiValueMap<String, String> urlParameterMap = MapUtil.convertHashMapToParamMap(urlParameterHashMap);

        api.resources().stream().filter(resource -> {
            boolean samePath = (basePath + resource.resourcePath()).equals(endpointPathPattern);
            boolean match = resource.methods().stream().anyMatch(ramlMethod ->
                    httpMethod.name().equalsIgnoreCase(ramlMethod.method()) && validateParametersMap(ramlMethod.headers(), valueMap)
                            && validateParametersMap(ramlMethod.queryParameters(), parameterValueMap) && validateBody(ramlMethod, httpMethod, body)
            );
            return samePath && match && validateParametersMap(resource.uriParameters(), urlParameterMap);
        }).findFirst().orElseThrow(() -> new RequestContextException(String.format("can not find endpoint '%s' from raml file", endpointPathPattern)));

        Object responseBody = null;
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
        return new HttpRequestContext(inputMessage.getHeaders(), parameterValueMap, urlParameterMap, responseBody);
    }

    private static boolean validateBody(Method ramlMethod, HttpMethod httpMethod, String body) {
        if (HttpMethod.POST != httpMethod && HttpMethod.PUT != httpMethod) {
            return true;
        }
        if (StringUtils.isBlank(body)) {
            throw new RequestContextException("body can not empty");
        }
        List<BodyLike> bodyLike = ramlMethod.body();

        if (bodyLike.size() == 1) {
            BodyLike like = bodyLike.get(0);
            if (like instanceof JSONBody) {
                JSONObject jsonObject = new JSONObject(new JSONTokener(body));
                String value = like.schema().value();
                Schema schema = SchemaLoader.load(new JSONObject(new JSONTokener(value)));
                schema.validate(jsonObject);
                return true;
            }
        }
        throw new RequestContextException("json body can not parsing");
    }

    public static boolean validateParametersMap(List<Parameter> parameters, MultiValueMap<String, String> keyValueMap) {
        for (Parameter parameter : parameters) {
            String key = parameter.name();
            List<String> entryValue = keyValueMap.get(key);
            if (parameter.required() && (entryValue == null || entryValue.isEmpty())) {
                throw new RequestContextException("Missing '" + key + "'");
            }
            if (entryValue.size() > 1 && !parameter.repeat()) {
                throw new RequestContextException("Repeating '" + key + "'");
            }
            for (String targetValue : entryValue) {
                validateParam(parameter, Optional.ofNullable(targetValue).orElse(parameter.defaultValue()));
            }
        }
        return true;
    }

    private static void validateParam(Parameter parameter, String keyValueMapOrDefault) {
        String key = parameter.name();
        if (parameter instanceof StringTypeDeclaration) {
            StringTypeDeclaration stringTypeDeclaration = (StringTypeDeclaration) parameter;
            List<String> enumValues = stringTypeDeclaration.enumValues();
            if (enumValues != null && enumValues.size() > 0 && !enumValues.contains(keyValueMapOrDefault)) {
                throw new RequestContextException(enumValues + " can not contains" + key);
            }
            Integer maxLength = stringTypeDeclaration.maxLength();
            if (maxLength != null && keyValueMapOrDefault.length() > maxLength) {
                throw new RequestContextException(key + " length can not >" + maxLength);
            }
            Integer minLength = stringTypeDeclaration.minLength();
            if (minLength != null && keyValueMapOrDefault.length() < minLength) {
                throw new RequestContextException(key + " length can not <" + minLength);
            }
            String pattern = stringTypeDeclaration.pattern();
            if (StringUtils.isNotEmpty(pattern) && !pattern.matches(keyValueMapOrDefault)) {
                throw new RequestContextException(key + " pattern can not matches" + pattern);
            }
        } else if (parameter instanceof NumberTypeDeclaration) {
            NumberTypeDeclaration numberTypeDeclaration = (NumberTypeDeclaration) parameter;
            BigDecimal valueOf = new BigDecimal(keyValueMapOrDefault);
            Double maximum = numberTypeDeclaration.maximum();
            if (maximum != null && valueOf.compareTo(BigDecimal.valueOf(maximum)) == 1) {
                throw new RequestContextException(key + " valueOf can not >" + maximum);
            }
            Double minimum = numberTypeDeclaration.minimum();
            if (minimum != null && valueOf.compareTo(BigDecimal.valueOf(minimum)) == -1) {
                throw new RequestContextException(key + " valueOf can not <" + minimum);
            }
        } else if (parameter instanceof BooleanTypeDeclaration) {
            Objects.requireNonNull(BooleanUtils.toBooleanObject(keyValueMapOrDefault), "can not match boolean type");
        } else if (parameter instanceof DateTypeDeclaration) {
            log.info("parameter type is DateTypeDeclaration");
        } else {
            throw new RequestContextException("Invalid param type " + parameter.type());
        }
    }

}
