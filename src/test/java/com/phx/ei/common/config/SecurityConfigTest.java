package com.phx.ei.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.INDIVIDUAL);
        user.setIdentifierType(IdentifierType.EMAIL);
        userRepository.save(user);
    }

    @Test
    void testAuthenticationManager_ValidCredentials() {
        String username = "testuser";
        String rawPassword = "password";
        
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
