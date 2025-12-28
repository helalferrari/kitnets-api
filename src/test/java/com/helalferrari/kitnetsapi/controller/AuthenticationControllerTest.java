package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.dto.auth.AuthenticationDTO;
import com.helalferrari.kitnetsapi.dto.auth.LoginResponseDTO;
import com.helalferrari.kitnetsapi.dto.auth.RegisterDTO;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.model.User;
import com.helalferrari.kitnetsapi.model.enums.UserRole;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    // Mocks auxiliares para o fluxo do Spring Security
    @Mock
    private Authentication authentication;

    @Mock
    private User userMock;

    @Test
    @DisplayName("Should login successfully and return token")
    void shouldLoginSuccessfully() {
        // Arrange
        String email = "teste@email.com";
        String password = "123";
        String token = "jwt.token.valid";
        AuthenticationDTO data = new AuthenticationDTO(email, password);

        // Simulamos o comportamento do AuthenticationManager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Simulamos que a autenticação retornou nosso User mockado
        when(authentication.getPrincipal()).thenReturn(userMock);

        // Simulamos dados do usuário
        when(userMock.getName()).thenReturn("Nome Teste");
        when(userMock.getRole()).thenReturn(UserRole.LANDLORD);

        // Simulamos a geração do token
        when(tokenService.generateToken(userMock)).thenReturn(token);

        // Act
        ResponseEntity response = authenticationController.login(data);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verifica o conteúdo do DTO de resposta
        LoginResponseDTO responseDTO = (LoginResponseDTO) response.getBody();
        assertEquals(token, responseDTO.token());
        assertEquals("Nome Teste", responseDTO.name());
        assertEquals("LANDLORD", responseDTO.role());
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterDTO data = new RegisterDTO(
                "Novo User",
                "novo@email.com",
                "senha123",
                UserRole.TENANT,
                "999",
                "000"
        );

        // Simula que NÃO existe ninguém com esse email (retorna null)
        when(userRepository.findByEmail(data.email())).thenReturn(null);

        // Act
        ResponseEntity response = authenticationController.register(data);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verifica se o userRepository.save() foi chamado com algum usuário
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return BadRequest when registering existing email")
    void shouldReturnBadRequestWhenRegisteringExistingEmail() {
        // Arrange
        RegisterDTO data = new RegisterDTO(
                "User Existente",
                "existente@email.com",
                "senha",
                UserRole.ADMIN,
                "111",
                "222"
        );

        // Simula que JÁ existe alguém (retorna qualquer objeto UserDetails)
        when(userRepository.findByEmail(data.email())).thenReturn(userMock);

        // Act
        ResponseEntity response = authenticationController.register(data);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Garante que NÃO tentou salvar nada
        verify(userRepository, never()).save(any(User.class));
    }
}