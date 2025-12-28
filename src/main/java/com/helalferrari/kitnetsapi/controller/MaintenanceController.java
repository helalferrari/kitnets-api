package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final FileStorageService fileStorageService;

    public MaintenanceController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/reprocess-thumbnails")
    // Idealmente proteger com @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> reprocessThumbnails() {
        new Thread(fileStorageService::reprocessAllThumbnails).start();
        return ResponseEntity.ok("Processamento de thumbnails iniciado em background.");
    }
}