package com.aleos.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ErrorDetails {

    private final List<String> errors;

    public static ErrorDetails fromSingleError(String message) {
        List<String> errorList = new ArrayList<>();
        errorList.add(message);
        return fromErrorList(errorList);
    }

    public static ErrorDetails fromErrorList(List<String> errorDetails) {
        return new ErrorDetails(errorDetails);
    }
}
