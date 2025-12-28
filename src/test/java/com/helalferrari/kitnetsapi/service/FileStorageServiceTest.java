package com.helalferrari.kitnetsapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        // Use reflection to set the root path to our temporary directory for testing
        ReflectionTestUtils.setField(fileStorageService, "root", tempDir);
        fileStorageService.init(); // Re-initialize with the new temporary path
    }

    @Test
    @DisplayName("init should create root directory if it does not exist")
    void init_CreatesDirectory() {
        assertTrue(Files.exists(tempDir));
        assertTrue(Files.isDirectory(tempDir));
    }

    @Test
    @DisplayName("save should store the file in a subdirectory named after the landlordId")
    void save_WithLandlordId() throws IOException {
        Long landlordId = 123L;
        String originalFilename = "test-image.jpg";
        MultipartFile file = new MockMultipartFile(
                "file",
                originalFilename,
                "image/jpeg",
                "test image content".getBytes()
        );

        String savedPath = fileStorageService.save(file, landlordId);

        // Check 1: The returned path should be correct
        assertTrue(savedPath.startsWith(landlordId + "/"));
        assertTrue(savedPath.endsWith("_" + originalFilename));

        // Check 2: The file should physically exist
        Path expectedFile = tempDir.resolve(savedPath);
        assertTrue(Files.exists(expectedFile));

        // Check 3: The content should be correct
        String content = Files.readString(expectedFile);
        assertEquals("test image content", content);
    }

    @Test
    @DisplayName("save should store the file in 'geral' subdirectory if landlordId is null")
    void save_WithNullLandlordId() throws IOException {
        String originalFilename = "another-image.png";
        MultipartFile file = new MockMultipartFile(
                "file",
                originalFilename,
                "image/png",
                "other content".getBytes()
        );

        String savedPath = fileStorageService.save(file, null);

        // Check 1: Path should be correct
        assertTrue(savedPath.startsWith("geral/"));
        assertTrue(savedPath.endsWith("_" + originalFilename));

        // Check 2: File should exist
        Path expectedFile = tempDir.resolve(savedPath);
        assertTrue(Files.exists(expectedFile));

        // Check 3: Content should be correct
        String content = Files.readString(expectedFile);
        assertEquals("other content", content);
    }

    @Test
    @DisplayName("save should throw RuntimeException on IOException")
    void save_ThrowsRuntimeException() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "fail.txt", "text/plain", "fail".getBytes());
        
        // Make the temp directory read-only to force an IOException
        tempDir.toFile().setReadOnly();

        // This test might not work on all OSes, but it's a common way to simulate this
        if (tempDir.toFile().canWrite()) {
             System.err.println("Skipping test 'save_ThrowsRuntimeException': Could not make temp directory read-only.");
             return;
        }

        assertThrows(RuntimeException.class, () -> {
            fileStorageService.save(file, 1L);
        });

        // Set it back to writable so @TempDir can clean up
        tempDir.toFile().setWritable(true);
    }
}