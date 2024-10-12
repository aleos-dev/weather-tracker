package com.aleos.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ErrorData {

    @Getter
    private final List<String> errors;

    public static ErrorData fromSingleError(String message) {
        List<String> errorList = new ArrayList<>();
        errorList.add(message);
        return fromErrorList(errorList);
    }

    public static ErrorData fromErrorList(List<String> errorDetails) {
        return new ErrorData(errorDetails);
    }
}
