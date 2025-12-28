package com.helalferrari.kitnetsapi.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    @DisplayName("Should create LoginResponseDTO with correct values")
    void shouldCreateLoginResponseDTO() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiJ9.token.crypto";
        String name = "Dono da Kitnet";
        String role = "LANDLORD";

        // Act
        LoginResponseDTO dto = new LoginResponseDTO(token, name, role);

        // Assert
        assertNotNull(dto);
        // Ordem correta: (Esperado, Atual)
        assertEquals(token, dto.token());
        assertEquals(name, dto.name());
        assertEquals(role, dto.role());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        LoginResponseDTO dto1 = new LoginResponseDTO("token1", "User A", "USER");
        LoginResponseDTO dto2 = new LoginResponseDTO("token1", "User A", "USER"); // Igual
        LoginResponseDTO dto3 = new LoginResponseDTO("token2", "User B", "ADMIN"); // Diferente

        // Assert Equals
        assertEquals(dto2, dto1); // dto2 é o esperado
        assertNotEquals(dto3, dto1); // dto3 é o esperado diferente

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
        LoginResponseDTO dto = new LoginResponseDTO("tk123", "Test Name", "TEST_ROLE");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("LoginResponseDTO"));
        assertTrue(stringResult.contains("tk123"));
        assertTrue(stringResult.contains("Test Name"));
        assertTrue(stringResult.contains("TEST_ROLE"));
    }
}