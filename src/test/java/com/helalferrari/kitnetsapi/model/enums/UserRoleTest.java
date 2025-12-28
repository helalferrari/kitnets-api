package com.helalferrari.kitnetsapi.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    @DisplayName("Should return correct string values for roles")
    void shouldReturnCorrectStringValues() {
        // Assert (Esperado, Atual)
        // Testa se o construtor atribuiu o valor certo e se o getRole() recupera
        assertEquals("admin", UserRole.ADMIN.getRole());
        assertEquals("landlord", UserRole.LANDLORD.getRole());
        assertEquals("tenant", UserRole.TENANT.getRole());
    }

    @Test
    @DisplayName("Should verify enum constants mapping")
    void shouldVerifyEnumConstants() {
        // Testa o método valueOf() gerado automaticamente pelo Java
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
        assertEquals(UserRole.LANDLORD, UserRole.valueOf("LANDLORD"));
        assertEquals(UserRole.TENANT, UserRole.valueOf("TENANT"));
    }

    @Test
    @DisplayName("Should contain all defined roles")
    void shouldContainAllRoles() {
        // Testa o método values() gerado automaticamente
        UserRole[] roles = UserRole.values();

        assertEquals(3, roles.length);
        assertTrue(containsRole(roles, UserRole.ADMIN));
        assertTrue(containsRole(roles, UserRole.LANDLORD));
        assertTrue(containsRole(roles, UserRole.TENANT));
    }

    // Helper simples para verificar array
    private boolean containsRole(UserRole[] roles, UserRole target) {
        for (UserRole role : roles) {
            if (role == target) return true;
        }
        return false;
    }
}