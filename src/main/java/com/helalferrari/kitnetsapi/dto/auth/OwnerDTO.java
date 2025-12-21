package com.helalferrari.kitnetsapi.dto.auth;

// Um record simples para devolver apenas o necessário do dono
public record OwnerDTO(Long id, String name, String email, String phone) {
}