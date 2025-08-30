package com.nelani.blog_land_backend.Util.Validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileValidation {

    public static String saveFile(String uploadDir, MultipartFile file) {
        if (file.isEmpty()) {
            throw  new IllegalArgumentException("No file uploaded");
        }

        // Ensure upload directory exists
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create upload folder at " + dir.getAbsolutePath());
        }

        // Generate unique filename
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Profile image update failed. Could not store file.", e);
        }

        return fileName;
    }
}