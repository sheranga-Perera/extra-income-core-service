package com.phx.ei_user.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void testAuthenticationManager_ValidCredentials() {
        // Create a test user with encoded password
        String username = "testuser";
        String rawPassword = "password";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken(username, rawPassword);
        
        Authentication authentication = authenticationManager.authenticate(authRequest);
        
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals(username, authentication.getName());
    }

    @Test
    void testAuthenticationManager_InvalidCredentials() {
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken("testuser", "wrongpassword");
        
        assertThrows(Exception.class, () -> {
            authenticationManager.authenticate(authRequest);
        });
    }

    @Test
    void testAuthenticationManager_NonExistentUser() {
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken("nonexistentuser", "password");
        
        assertThrows(Exception.class, () -> {
            authenticationManager.authenticate(authRequest);
        });
    }

    @Test
    void testSecurityConfiguration_BeansCreated() {
        // This test verifies that the security configuration beans are created successfully
        assertNotNull(authenticationManager);
    }
} 