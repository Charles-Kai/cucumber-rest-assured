package com.example.demo.raml;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestMvcErrorAdvice {
    @ExceptionHandler(RequestContextException.class)
    public ResponseEntity<ErrorPayload> onRuntime(RequestContextException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorPayload().withAddErrorInfoItem(new ErrorItem("RAML_1000", e.getMessage())));
    }

}
