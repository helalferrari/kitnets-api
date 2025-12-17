package com.helalferrari.kitnetsapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

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

            // 4. Salva o arquivo dentro da subpasta
            Files.copy(file.getInputStream(), destinationDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            // 5. Retorna o caminho relativo para salvar no banco (ex: 10/foto-xyz.jpg)
            return subFolderName + "/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}