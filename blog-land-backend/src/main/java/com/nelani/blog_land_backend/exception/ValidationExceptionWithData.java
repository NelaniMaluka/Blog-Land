package com.nelani.blog_land_backend.exception;

import jakarta.validation.ValidationException;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationExceptionWithData extends ValidationException {
    private final Map<String, Double> flagged;

    public ValidationExceptionWithData(String message, Map<String, Double> flagged) {
        super(message);
        this.flagged = flagged;
    }

}
