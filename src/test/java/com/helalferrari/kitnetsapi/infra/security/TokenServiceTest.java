package com.helalferrari.kitnetsapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.helalferrari.kitnetsapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;
    private static final String SECRET = "test_secret";

    @BeforeEach
    void setUp() {
        // Inject the secret value before each test
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Should generate a valid token on success")
    void testGenerateToken_Success() {
        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String subject = tokenService.validateToken(token);
        assertEquals(user.getEmail(), subject);
    }

    @Test
    @DisplayName("Should return an empty string for an invalid token")
    void testValidateToken_InvalidToken() {
        String subject = tokenService.validateToken("invalid.token");
        assertTrue(subject.isEmpty());
    }

    @Test
    @DisplayName("Should return an empty string for an expired token")
    void testValidateToken_ExpiredToken() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String expiredToken = JWT.create()
                .withIssuer("kitnets-api")
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().minusSeconds(10))
                .sign(algorithm);

        String subject = tokenService.validateToken(expiredToken);
        assertTrue(subject.isEmpty());
    }

    @Test
    @DisplayName("Should throw RuntimeException when token generation fails with JWTCreationException")
    void shouldThrowExceptionWhenTokenGenerationFails() {
        // Using MockedStatic to force JWTCreationException
        try (MockedStatic<JWT> jwtMock = mockStatic(JWT.class)) {
            // Mock JWT.create() to return a mock builder, or throw exception directly if feasible.
            // But JWT.create() returns a Builder. The exception usually happens at .sign().
            
            JWTCreator.Builder builderMock = mock(JWTCreator.Builder.class);
            jwtMock.when(JWT::create).thenReturn(builderMock);
            
            // Mock chained calls
            when(builderMock.withIssuer(anyString())).thenReturn(builderMock);
            when(builderMock.withSubject(anyString())).thenReturn(builderMock);
            when(builderMock.withExpiresAt(any(Instant.class))).thenReturn(builderMock);
            
            // Force exception on sign()
            when(builderMock.sign(any(Algorithm.class)))
                    .thenThrow(new JWTCreationException("Error creating token", new Throwable()));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                tokenService.generateToken(user);
            });

            assertEquals("Error while generating token", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should generate a token with correct expiration date")
    void testGenerateToken_Expiration() {
        String token = tokenService.generateToken(user);
        
        Instant expectedExpiration = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        Instant actualExpiration = JWT.decode(token).getExpiresAt().toInstant();

        // Allow a small tolerance for the execution time (e.g., 5 seconds)
        long diff = Math.abs(expectedExpiration.getEpochSecond() - actualExpiration.getEpochSecond());
        assertTrue(diff < 5, "Expiration time difference is too large: " + diff);
    }
}