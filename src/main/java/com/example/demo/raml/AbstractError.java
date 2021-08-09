package com.example.demo.raml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public abstract class AbstractError implements Serializable {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}
