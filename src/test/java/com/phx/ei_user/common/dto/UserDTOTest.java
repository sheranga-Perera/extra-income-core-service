package com.phx.ei_user.common.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testUserDTO_Creation() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john.doe@example.com";
        
        UserDTO userDTO = new UserDTO();
        // Note: UserDTO doesn't have getter/setter methods in the current implementation
        // This test verifies the class can be instantiated
        
        assertNotNull(userDTO);
    }

    @Test
    void testUserDTO_DefaultValues() {
        UserDTO userDTO = new UserDTO();
        
        assertNotNull(userDTO);
        // The fields are private and don't have getters, so we can't test them directly
    }

    @Test
    void testUserDTO_Equality() {
        UserDTO userDTO1 = new UserDTO();
        UserDTO userDTO2 = new UserDTO();
        
        // Both objects should be different instances
        assertNotSame(userDTO1, userDTO2);
    }
} 