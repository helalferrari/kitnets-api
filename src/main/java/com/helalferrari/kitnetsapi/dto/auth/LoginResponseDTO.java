package com.helalferrari.kitnetsapi.dto.auth;

// Agora o DTO devolve token, nome e role
public record LoginResponseDTO(String token, String name, String role) {
}