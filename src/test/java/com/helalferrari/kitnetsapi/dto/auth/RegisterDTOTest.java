package com.helalferrari.kitnetsapi.dto.auth;

import com.helalferrari.kitnetsapi.model.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterDTOTest {

    @Test
    @DisplayName("Should create RegisterDTO with correct values")
    void shouldCreateRegisterDTO() {
        // Arrange
        String name = "Novo Usuario";
        String email = "novo@email.com";
        String password = "senhaForte";
        UserRole role = UserRole.LANDLORD;
        String phone = "48 99999-9999";
        String cpf = "123.456.789-00";

        // Act
        RegisterDTO dto = new RegisterDTO(name, email, password, role, phone, cpf);

        // Assert
        assertNotNull(dto);
        // Ordem Sonar: (Esperado, Atual)
        assertEquals(name, dto.name());
        assertEquals(email, dto.email());
        assertEquals(password, dto.password());
        assertEquals(role, dto.role());
        assertEquals(phone, dto.phone());
        assertEquals(cpf, dto.cpf());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        // DTO Base
        RegisterDTO dto1 = new RegisterDTO("User", "mail", "pass", UserRole.LANDLORD, "111", "000");

        // DTO Igual
        RegisterDTO dto2 = new RegisterDTO("User", "mail", "pass", UserRole.LANDLORD, "111", "000");

        // DTO Diferente (Mudando o email)
        RegisterDTO dto3 = new RegisterDTO("User", "other@mail", "pass", UserRole.LANDLORD, "111", "000");

        // Assert Equals
        // (Esperado, Atual)
        assertEquals(dto2, dto1);
        assertNotEquals(dto3, dto1);

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
        RegisterDTO dto = new RegisterDTO("Test Name", "t@mail.com", "p123", UserRole.LANDLORD, "999", "111.222.333-44");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("RegisterDTO"));
        assertTrue(stringResult.contains("Test Name"));
        assertTrue(stringResult.contains("t@mail.com"));
        assertTrue(stringResult.contains("LANDLORD"));
        assertTrue(stringResult.contains("111.222.333-44"));
    }
}