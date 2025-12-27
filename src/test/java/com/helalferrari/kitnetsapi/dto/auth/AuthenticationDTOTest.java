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
        // Ordem correta: (Esperado, Atual)
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
        // dto2 é a nossa referência de "esperado", dto1 é o objeto que estamos testando
        assertEquals(dto2, dto1);

        // dto3 é a referência diferente (esperado), dto1 é o atual
        assertNotEquals(dto3, dto1);

        // CORREÇÃO DO SONAR AQUI:
        // O valor 'null' ou 'new Object()' é o esperado (o que estamos comparando contra)
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
        AuthenticationDTO dto = new AuthenticationDTO("string@test.com", "passString");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("AuthenticationDTO"));
        assertTrue(stringResult.contains("string@test.com"));
        assertTrue(stringResult.contains("passString"));
    }
}