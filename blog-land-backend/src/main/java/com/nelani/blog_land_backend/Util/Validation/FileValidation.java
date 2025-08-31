package com.nelani.blog_land_backend.Util.Validation;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileValidation {

    public static String removeFile(String file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        Path path = Paths.get(file);

        try {
            Files.delete(path);
            return "File deleted successfully";
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("No such file: " + file);
        } catch (DirectoryNotEmptyException e) {
            throw new IllegalArgumentException("Cannot delete, directory is not empty: " + file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to delete file.");
        }

    }

    public static String saveFile(String uploadDir, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
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