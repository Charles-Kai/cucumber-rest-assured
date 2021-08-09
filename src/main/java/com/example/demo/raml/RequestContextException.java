package com.example.demo.raml;

public class RequestContextException extends RuntimeException{
    public RequestContextException(String message) {
        super(message);
    }

    public RequestContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
