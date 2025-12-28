package com.helalferrari.kitnetsapi.infra.security;

import com.helalferrari.kitnetsapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    // Garante que o contexto de segurança comece limpo antes de cada teste
    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    // Garante que limpamos depois também para não afetar outros testes
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate user when token is valid")
    void shouldAuthenticateWhenTokenIsValid() throws ServletException, IOException {
        // Arrange
        String token = "valid.token.jwt";
        String login = "user@test.com";

        // Simula o Header Authorization
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Simula a validação do TokenService
        when(tokenService.validateToken(token)).thenReturn(login);

        // Simula a busca no Banco de Dados
        when(userRepository.findByEmail(login)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // Act
        // Chamamos o método protected (possível porque o teste está no mesmo pacote)
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verifica se o usuário foi colocado no contexto de segurança (autenticado)
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Verifica se o filtro continuou a execução (muito importante!)
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is missing")
    void shouldNotAuthenticateWhenTokenIsMissing() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        // O filtro deve continuar mesmo sem token (para cair no 403 depois)
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should not authenticate when token is invalid")
    void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // TokenService retorna string vazia ou nula para token inválido
        when(tokenService.validateToken(token)).thenReturn("");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should not authenticate when user is not found")
    void shouldNotAuthenticateWhenUserNotFound() throws ServletException, IOException {
        // Arrange
        String token = "valid.token";
        String login = "ghost@user.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(login);
        // Usuário não existe no banco
        when(userRepository.findByEmail(login)).thenReturn(null);

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}