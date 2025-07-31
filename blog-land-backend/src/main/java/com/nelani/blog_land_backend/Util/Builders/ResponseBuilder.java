package com.nelani.blog_land_backend.Util.Builders;

import com.nelani.blog_land_backend.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    public static ResponseEntity<?> invalid(String title, String message) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(title,message));
    }

    public static ResponseEntity<?> unauthorized(String title, String message) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(title,message));
    }

    public static ResponseEntity<?> serverError() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error",
                        "An unexpected error occurred. Please try again later."));
    }

    public static ResponseEntity<?> serverError(String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error",
                        "An unexpected error occurred. Please try again later."));
    }

}
