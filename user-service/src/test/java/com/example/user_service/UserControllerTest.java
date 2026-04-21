package com.example.user_service;

import com.example.user_service.controller.UserController;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Stefan");
        user.setLastName("Milinkov");
        user.setEmail("stefan@mail.com");

        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("stefan@mail.com", response.getBody().getEmail());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("Stefan");
        user.setLastName("Milinkov");
        user.setEmail("stefan@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() != null);
        assertEquals(userId, response.getBody().getId());

        verify(userRepository, times(1)).findById(userId);
    }
}
