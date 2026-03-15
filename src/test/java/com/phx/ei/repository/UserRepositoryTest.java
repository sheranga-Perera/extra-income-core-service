package com.phx.ei.repository;

import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.entity.User;
import com.phx.ei.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import com.phx.ei.common.security.Role;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setPassword("encodedpassword");
        testUser.setRole(com.phx.ei.common.security.Role.INDIVIDUAL);
        testUser.setIdentifierType(IdentifierType.EMAIL);
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedpassword", savedUser.getPassword());
        assertEquals(Role.INDIVIDUAL, savedUser.getRole());
        assertEquals(IdentifierType.EMAIL, savedUser.getIdentifierType());
    }

    @Test
    void testFindByUsername_UserExists() {
        userRepository.save(testUser);
        
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotExists() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_NullUsername() {
        Optional<User> foundUser = userRepository.findByUsername(null);
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_EmptyUsername() {
        Optional<User> foundUser = userRepository.findByUsername("");
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindById_UserExists() {
        User savedUser = userRepository.save(testUser);
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    void testFindById_UserNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<User> foundUser = userRepository.findById(nonExistentId);
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteUser() {
        User savedUser = userRepository.save(testUser);
        
        userRepository.delete(savedUser);
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        User savedUser = userRepository.save(testUser);
        
        savedUser.setRole(com.phx.ei.common.security.Role.ADMIN);
        User updatedUser = userRepository.save(savedUser);
        
        assertEquals(Role.ADMIN, updatedUser.getRole());
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(Role.ADMIN, foundUser.get().getRole());
    }
} 
