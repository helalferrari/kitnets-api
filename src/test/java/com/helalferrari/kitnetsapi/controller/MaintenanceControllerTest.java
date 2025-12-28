package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.service.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MaintenanceControllerTest {

    @InjectMocks
    private MaintenanceController maintenanceController;

    @Mock
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("Should trigger thumbnail reprocessing asynchronously")
    void shouldTriggerReprocessing() {
        // Act
        ResponseEntity<String> response = maintenanceController.reprocessThumbnails();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Processamento de thumbnails iniciado em background.", response.getBody());

        // Verify that the service method was called
        // Since it's in a new Thread, we use timeout to wait for it (async verification)
        verify(fileStorageService, timeout(1000)).reprocessAllThumbnails();
    }
}