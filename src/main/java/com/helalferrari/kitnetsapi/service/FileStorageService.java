package com.helalferrari.kitnetsapi.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");
    private static final int THUMBNAIL_WIDTH = 300;

    public FileStorageService() {
        init();
    }

    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar pasta de uploads!");
        }
    }

    // Agora aceita o ID do Dono para criar a subpasta
    public String save(MultipartFile file, Long landlordId) {
        try {
            // 1. Define o nome da subpasta (ID do dono ou "geral" se nulo)
            String subFolderName = (landlordId != null) ? String.valueOf(landlordId) : "geral";

            // 2. Cria o caminho completo: uploads/10/
            Path destinationDir = this.root.resolve(subFolderName);
            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            // 3. Gera nome único para o arquivo
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = destinationDir.resolve(filename);

            // 4. Salva o arquivo original dentro da subpasta
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Gera o Thumbnail
            createThumbnail(filePath.toFile(), destinationDir);

            // 6. Retorna o caminho relativo para salvar no banco (ex: 10/foto-xyz.jpg)
            return subFolderName + "/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    private void createThumbnail(File originalFile, Path destinationDir) {
        try {
            // Cria subpasta thumbnails se não existir
            Path thumbDir = destinationDir.resolve("thumbnails");
            if (!Files.exists(thumbDir)) {
                Files.createDirectories(thumbDir);
            }

            // Lê a imagem original
            BufferedImage originalImage = ImageIO.read(originalFile);
            if (originalImage == null) return; // Não é uma imagem suportada

            // Redimensiona (Mantendo proporção)
            BufferedImage thumbnail = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, THUMBNAIL_WIDTH);

            // Salva o thumbnail com o mesmo nome
            File thumbFile = thumbDir.resolve(originalFile.getName()).toFile();
            
            // Extrai a extensão
            String fileName = originalFile.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            ImageIO.write(thumbnail, extension, thumbFile);

        } catch (Exception e) {
            System.err.println("Erro ao gerar thumbnail para " + originalFile.getName() + ": " + e.getMessage());
            // Não lançamos erro crítico para não impedir o upload principal caso o thumb falhe
        }
    }

    // Método utilitário para reprocessar tudo (Script de migração)
    public void reprocessAllThumbnails() {
        try (Stream<Path> landlordDirs = Files.list(root)) {
            landlordDirs.filter(Files::isDirectory).forEach(landlordDir -> {
                try (Stream<Path> files = Files.list(landlordDir)) {
                    files.filter(Files::isRegularFile).forEach(file -> {
                        // Verifica se é imagem (extensão básica)
                        String name = file.getFileName().toString().toLowerCase();
                        if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")) {
                            System.out.println("Processando: " + name);
                            createThumbnail(file.toFile(), landlordDir);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}