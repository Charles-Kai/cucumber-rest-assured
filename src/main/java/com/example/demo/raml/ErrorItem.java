package com.example.demo.raml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ErrorItem implements Serializable {

    private String code;

    private List<String> causes = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public ErrorItem(String code, String cause) {
        this.code = code;
        this.causes.add(cause);
    }
}
