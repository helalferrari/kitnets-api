package com.helalferrari.kitnetsapi.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OwnerDTOTest {

    @Test
    @DisplayName("Should create OwnerDTO with correct values")
    void shouldCreateOwnerDTO() {
        // Arrange
        Long id = 1L;
        String name = "Carlos Helal";
        String email = "carlos@kitnet.com";
        String phone = "48 99999-9999";

        // Act
        OwnerDTO dto = new OwnerDTO(id, name, email, phone);

        // Assert
        assertNotNull(dto);
        // Ordem Sonar: (Esperado, Atual)
        assertEquals(id, dto.id());
        assertEquals(name, dto.name());
        assertEquals(email, dto.email());
        assertEquals(phone, dto.phone());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        OwnerDTO dto1 = new OwnerDTO(1L, "User A", "email@a.com", "111");
        OwnerDTO dto2 = new OwnerDTO(1L, "User A", "email@a.com", "111"); // Igual
        OwnerDTO dto3 = new OwnerDTO(2L, "User B", "email@b.com", "222"); // Diferente

        // Assert Equals
        assertEquals(dto2, dto1);
        assertNotEquals(dto3, dto1);

        // Verificações de robustez (null e outro tipo)
        assertNotEquals(null, dto1);
        assertNotEquals(new Object(), dto1);

        // Assert HashCode
        assertEquals(dto2.hashCode(), dto1.hashCode());
        assertNotEquals(dto3.hashCode(), dto1.hashCode());
    }

    @Test
    @DisplayName("Should return correct String representation")
    void shouldReturnCorrectToString() {
        // Arrange
        OwnerDTO dto = new OwnerDTO(55L, "Test Name", "test@mail.com", "000");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("OwnerDTO"));
        assertTrue(stringResult.contains("55"));
        assertTrue(stringResult.contains("Test Name"));
        assertTrue(stringResult.contains("test@mail.com"));
        assertTrue(stringResult.contains("000"));
    }
}