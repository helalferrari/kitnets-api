package com.helalferrari.kitnetsapi.service;

import com.helalferrari.kitnetsapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Should return user details when email is found")
    void shouldReturnUserDetailsWhenFound() {
        // Arrange
        String email = "test@email.com";
        UserDetails mockUser = Mockito.mock(UserDetails.class);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(mockUser);

        // Act
        UserDetails result = authorizationService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);
        Mockito.verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when email is not found")
    void shouldThrowExceptionWhenNotFound() {
        // Arrange
        String email = "notfound@email.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(email);
        });

        assertTrue(exception.getMessage().contains(email));
        Mockito.verify(userRepository).findByEmail(email);
    }
}
