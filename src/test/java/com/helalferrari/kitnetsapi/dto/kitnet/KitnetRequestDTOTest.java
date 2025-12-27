package com.helalferrari.kitnetsapi.dto.kitnet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KitnetRequestDTOTest {

    @Test
    @DisplayName("Should create KitnetRequestDTO with correct values")
    void shouldCreateKitnetRequestDTO() {
        // Arrange
        String nome = "Kitnet Beira Mar";
        Double valor = 1200.50;
        Integer vagas = 1;
        Double taxa = 50.0;
        String descricao = "Vista para o mar";
        Long userId = 10L;

        // Act
        KitnetRequestDTO dto = new KitnetRequestDTO(nome, valor, vagas, taxa, descricao, userId);

        // Assert
        assertNotNull(dto);
        assertEquals(nome, dto.nome());
        assertEquals(valor, dto.valor());
        assertEquals(vagas, dto.vagas());
        assertEquals(taxa, dto.taxa());
        assertEquals(descricao, dto.descricao());
        assertEquals(userId, dto.userId());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        KitnetRequestDTO dto1 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 1L);
        KitnetRequestDTO dto2 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 1L); // Igual
        KitnetRequestDTO dto3 = new KitnetRequestDTO("K2", 200.0, 2, 20.0, "D2", 2L); // Diferente

        // Assert Equals
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());

        // Assert HashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Should return correct String representation")
    void shouldReturnCorrectToString() {
        // Arrange
        KitnetRequestDTO dto = new KitnetRequestDTO("Kitnet String", 500.0, 0, 0.0, "Desc", 5L);

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("KitnetRequestDTO"));
        assertTrue(stringResult.contains("Kitnet String"));
        assertTrue(stringResult.contains("500.0"));
        assertTrue(stringResult.contains("userId=5"));
    }
}