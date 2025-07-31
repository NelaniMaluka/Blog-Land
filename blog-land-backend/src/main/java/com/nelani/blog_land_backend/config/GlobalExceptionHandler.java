package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.Util.Builders.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError() {
        return ResponseBuilder.serverError();
    }
}
