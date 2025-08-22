package com.phx.ei_user.repository;

import com.phx.ei_user.common.entity.User;
import com.phx.ei_user.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        testUser.setRole("ROLE_USER");
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedpassword", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
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
        
        savedUser.setRole("ROLE_ADMIN");
        User updatedUser = userRepository.save(savedUser);
        
        assertEquals("ROLE_ADMIN", updatedUser.getRole());
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("ROLE_ADMIN", foundUser.get().getRole());
    }
} 