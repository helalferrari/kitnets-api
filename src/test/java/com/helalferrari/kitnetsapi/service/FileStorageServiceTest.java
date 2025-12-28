package com.helalferrari.kitnetsapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        
        // Criar uma imagem válida em memória
        byte[] imageBytes = createValidImageBytes("jpg");

        MultipartFile file = new MockMultipartFile(
                "file",
                originalFilename,
                "image/jpeg",
                imageBytes
        );

        String savedPath = fileStorageService.save(file, landlordId);

        // Verifica o arquivo original
        assertTrue(savedPath.startsWith(landlordId + "/"));
        Path expectedFile = tempDir.resolve(savedPath);
        assertTrue(Files.exists(expectedFile));

        // Verifica o Thumbnail
        Path thumbDir = tempDir.resolve(String.valueOf(landlordId)).resolve("thumbnails");
        assertTrue(Files.exists(thumbDir), "Diretório de thumbnails deve existir");
        
        String savedFilename = expectedFile.getFileName().toString();
        Path thumbFile = thumbDir.resolve(savedFilename);
        assertTrue(Files.exists(thumbFile), "Arquivo de thumbnail deve existir");
    }

    @Test
    @DisplayName("save should handle invalid image gracefully (no thumbnail)")
    void save_WithInvalidImage() throws IOException {
        Long landlordId = 999L;
        MultipartFile file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "not an image".getBytes()
        );

        String savedPath = fileStorageService.save(file, landlordId);

        // O arquivo original deve ser salvo
        Path expectedFile = tempDir.resolve(savedPath);
        assertTrue(Files.exists(expectedFile));

        // Thumbnail NÃO deve ser gerado para texto
        Path thumbDir = tempDir.resolve(String.valueOf(landlordId)).resolve("thumbnails");
        String savedFilename = expectedFile.getFileName().toString();
        // thumbDir pode até ser criado dependendo da impl, mas o arquivo dentro não deve existir
        if (Files.exists(thumbDir)) {
            assertFalse(Files.exists(thumbDir.resolve(savedFilename)), "Thumbnail não deve ser criado para arquivos de texto");
        }
    }

    // Helper para criar bytes de imagem real
    private byte[] createValidImageBytes(String format) throws IOException {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, format, baos);
        return baos.toByteArray();
    }
}