package com.helalferrari.kitnetsapi.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationDTOTest {

    @Test
    @DisplayName("Should create AuthenticationDTO with correct values")
    void shouldCreateAuthenticationDTO() {
        // Arrange
        String email = "teste@email.com";
        String password = "senha123";

        // Act
        AuthenticationDTO authDTO = new AuthenticationDTO(email, password);

        // Assert
        assertNotNull(authDTO);
        assertEquals(email, authDTO.email());
        assertEquals(password, authDTO.password());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        AuthenticationDTO dto1 = new AuthenticationDTO("user@mail.com", "pass");
        AuthenticationDTO dto2 = new AuthenticationDTO("user@mail.com", "pass"); // Igual ao 1
        AuthenticationDTO dto3 = new AuthenticationDTO("other@mail.com", "123"); // Diferente

        // Assert Equals
        assertEquals(dto1, dto2); // Deve ser igual
        assertNotEquals(dto1, dto3); // Deve ser diferente
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
        AuthenticationDTO dto = new AuthenticationDTO("string@test.com", "passString");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("AuthenticationDTO"));
        assertTrue(stringResult.contains("string@test.com"));
        assertTrue(stringResult.contains("passString"));
    }
}