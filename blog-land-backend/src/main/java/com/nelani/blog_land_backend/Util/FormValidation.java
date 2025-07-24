package com.nelani.blog_land_backend.Util;

public class FormValidation {

    public static String validatedEmail(String providedEmail) {
        String email = FormValidation.trimAndValidate(providedEmail, "Email");

        // Validate email format
        if (!FormValidation.isValidEmail(email)) {
            throw  new IllegalArgumentException(
                    "The provided email address is not valid. Please provide a valid email address.");
        }

        return email;
    }

    public static String validatedPassword(String providedPassword) {
        String password = FormValidation.trimAndValidate(providedPassword, "Password");

        // Checks if the password is valid
        if (!FormValidation.isValidPassword(password)) {
            throw  new IllegalArgumentException(
                    "Password must contain at least 8 characters, an uppercase letter, a digit, and a special character.");
        }

        return password;
    }

    public static String validatedPassword(String providedPassword, String fieldName) {
        String password = FormValidation.trimAndValidate(providedPassword, "Password");

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

    public static String trimAndValidate(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return field.trim();
    }
}
