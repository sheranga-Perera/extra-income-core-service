package com.phx.ei_user.common.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testRegisterRequest_Creation() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword123");
        
        assertEquals("newuser", registerRequest.getUsername());
        assertEquals("newpassword123", registerRequest.getPassword());
    }

    @Test
    void testRegisterRequest_DefaultValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getPassword());
    }

    @Test
    void testRegisterRequest_EmptyValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setPassword("");
        
        assertEquals("", registerRequest.getUsername());
        assertEquals("", registerRequest.getPassword());
    }

    @Test
    void testRegisterRequest_NullValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(null);
        registerRequest.setPassword(null);
        
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getPassword());
    }
} 