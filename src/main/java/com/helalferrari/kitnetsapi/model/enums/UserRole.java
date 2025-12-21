package com.helalferrari.kitnetsapi.model.enums;

public enum UserRole {
    ADMIN("admin"),
    LANDLORD("landlord"), // Proprietário
    TENANT("tenant");     // Inquilino

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}