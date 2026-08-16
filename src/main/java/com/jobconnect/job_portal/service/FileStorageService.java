package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "doc", "docx");

    private Path getUploadPath() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory", e);
        }
        return path;
    }

    public String storeFile(MultipartFile file, Long ownerId) {
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume"
        );

        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileStorageException(
                    "Invalid file type: ." + extension + ". Allowed types: " + ALLOWED_EXTENSIONS);
        }

        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        // Unique name: candidateId + UUID prevents collisions and guessable URLs
        String storedFilename = ownerId + "_" + UUID.randomUUID() + "." + extension;

        try {
            Path targetPath = getUploadPath().resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + originalFilename, e);
        }
    }

    public InputStream loadFileAsStream(String filename) {
        try {
            Path filePath = getUploadPath().resolve(filename).normalize();
            if (!filePath.startsWith(getUploadPath())) {
                throw new FileStorageException("Cannot access file outside upload directory");
            }
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new FileStorageException("File not found: " + filename, e);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
    }
}