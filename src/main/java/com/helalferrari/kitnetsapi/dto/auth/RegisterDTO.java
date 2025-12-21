package com.helalferrari.kitnetsapi.dto.auth;

import com.helalferrari.kitnetsapi.model.enums.UserRole;

public record RegisterDTO(String name, String email, String password, UserRole role, String phone, String cpf) {
}