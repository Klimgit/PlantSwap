package com.plantswap.auth.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        List<FieldError> errors
) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now(), null);
    }

    public static ErrorResponse withFields(int status, String error,
                                            String message, List<FieldError> fields) {
        return new ErrorResponse(status, error, message, Instant.now(), fields);
    }

    public record FieldError(String field, String message) {}
}
