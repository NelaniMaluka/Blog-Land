package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Provider;

public class FormValidation {

    public static String assertValidatedEmail(String providedEmail) {
        String email = FormValidation.assertRequiredField(providedEmail, "Email");

        // Validate email format
        if (!FormValidation.isValidEmail(email)) {
            throw  new IllegalArgumentException(
                    "The provided email address is not valid. Please provide a valid email address.");
        }

        return email;
    }

    public static String assertValidatedPassword(String providedPassword) {
        String password = FormValidation.assertRequiredField(providedPassword, "Password");

        // Checks if the password is valid
        if (!FormValidation.isValidPassword(password)) {
            throw  new IllegalArgumentException(
                    "Password must contain at least 8 characters, an uppercase letter, a digit, and a special character.");
        }

        return password;
    }

    public static String assertValidatedPassword(String providedPassword, String fieldName) {
        String password = FormValidation.assertRequiredField(providedPassword, "Password");

        // Checks if the password is valid
        if (!FormValidation.isValidPassword(password)) {
            throw  new IllegalArgumentException(
                    fieldName + " must contain at least 8 characters, an uppercase letter, a digit, and a special character.");
        }

        return password;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        return email.matches(emailRegex);
    }

    private static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(passwordRegex);
    }

    public static String assertRequiredField(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return field.trim();
    }

    public static Long assertRequiredField(Long field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(fieldName + field + " is required.");
        }
        return field;
    }

    public static Provider assertRequiredField(Provider field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(fieldName + field + " is required.");
        }
        return field;
    }

}
