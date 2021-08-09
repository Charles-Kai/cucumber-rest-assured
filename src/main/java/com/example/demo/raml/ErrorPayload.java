package com.example.demo.raml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ErrorPayload extends AbstractError implements Serializable {


    private List<ErrorItem> errorInfo = new ArrayList<>();

    public List<ErrorItem> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(List<ErrorItem> errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ErrorPayload withAddErrorInfoItem(ErrorItem errorItem){
        this.errorInfo.add(errorItem);
        return this;
    }
}
