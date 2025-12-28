package com.helalferrari.kitnetsapi.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationsTest {

    @InjectMocks
    private SecurityConfigurations securityConfigurations;

    @Mock
    private SecurityFilter securityFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpSecurity httpSecurity;

    @Test
    @DisplayName("Should return BCryptPasswordEncoder")
    void shouldReturnBCryptPasswordEncoder() {
        // Act
        PasswordEncoder encoder = securityConfigurations.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("Should return AuthenticationManager")
    void shouldReturnAuthenticationManager() throws Exception {
        // Arrange
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authManagerMock);

        // Act
        AuthenticationManager result = securityConfigurations.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertEquals(authManagerMock, result);
    }

    @Test
    @DisplayName("Should configure CORS correctly")
    void shouldConfigureCorsCorrectly() {
        // Act
        CorsConfigurationSource source = securityConfigurations.corsConfigurationSource();

        // Simula uma requisição para extrair a configuração
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        // Assert
        assertNotNull(config);

        // Verifica Origens
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));

        // Verifica Métodos
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowedMethods().contains("PUT"));
        assertTrue(config.getAllowedMethods().contains("DELETE"));

        // Verifica Headers
        assertEquals("*", config.getAllowedHeaders().get(0));
    }

    @Test
    @DisplayName("Should build SecurityFilterChain")
    void shouldBuildSecurityFilterChain() throws Exception {
        // Arrange
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);

        // CORREÇÃO AQUI: Mockamos a classe concreta DefaultSecurityFilterChain
        DefaultSecurityFilterChain chainMock = mock(DefaultSecurityFilterChain.class);

        // Agora os tipos batem!
        when(httpSecurity.build()).thenReturn(chainMock);

        // Act
        SecurityFilterChain result = securityConfigurations.securityFilterChain(httpSecurity);

        // Assert
        assertNotNull(result);
        assertEquals(chainMock, result);

        verify(httpSecurity).csrf(any());
        verify(httpSecurity).cors(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(eq(securityFilter), any());
    }
}