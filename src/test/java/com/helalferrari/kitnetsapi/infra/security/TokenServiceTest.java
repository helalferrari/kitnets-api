package com.helalferrari.kitnetsapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.helalferrari.kitnetsapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        // Inject the secret value before each test
        ReflectionTestUtils.setField(tokenService, "secret", "test_secret");
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
        Algorithm algorithm = Algorithm.HMAC256("test_secret");
        String expiredToken = JWT.create()
                .withIssuer("kitnets-api")
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().minusSeconds(10))
                .sign(algorithm);

        String subject = tokenService.validateToken(expiredToken);
        assertTrue(subject.isEmpty());
    }

    @Test
    @DisplayName("Should throw RuntimeException when token generation fails")
    void testGenerateToken_ThrowsRuntimeException() {
        // Set secret to null to force JWTCreationException
        ReflectionTestUtils.setField(tokenService, "secret", null);

        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(user);
        });
    }

    @Test
    @DisplayName("Should generate a token with correct expiration date")
    void testGenerateToken_Expiration() {
        String token = tokenService.generateToken(user);
        
        Instant expectedExpiration = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        Instant actualExpiration = JWT.decode(token).getExpiresAt().toInstant();

        // Allow a small tolerance for the execution time
        assertTrue(Math.abs(expectedExpiration.getEpochSecond() - actualExpiration.getEpochSecond()) < 5);
    }
}