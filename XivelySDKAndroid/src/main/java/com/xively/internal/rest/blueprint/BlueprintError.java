package com.xively.internal.rest.blueprint;


public class BlueprintError {
    public String code;
    public String message;
    public String innerError;

    @Override
    public String toString() {
        return "BlueprintError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", innerError='" + innerError + '\'' +
                '}';
    }
}
