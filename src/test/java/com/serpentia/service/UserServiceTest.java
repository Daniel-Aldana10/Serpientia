package com.serpentia.service;

import com.serpentia.dto.RegisterRequest;
import com.serpentia.dto.UserStatistics;
import com.serpentia.model.User;
import com.serpentia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testLoadUserByUsername_found() {
        User user = new User("user1", "email@test.com", "pass");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        UserDetails details = userService.loadUserByUsername("user1");
        assertEquals("user1", details.getUsername());
    }

    @Test
    void testRegisterUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user2");
        req.setEmail("email2@test.com");
        req.setPassword("pass");
        when(userRepository.findByUsername("user2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("email2@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        userService.registerUser(req);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserStats_updatesStats() {
        User user = new User("user3", "email3@test.com", "pass");
        when(userRepository.findByUsername("user3")).thenReturn(Optional.of(user));
        userService.updateUserStats("user3", 100, true);
        verify(userRepository).save(user);
        assertEquals(1, user.getGamesPlayed());
        assertEquals(1, user.getGamesWon());
        assertEquals(100, user.getTotalPoints());
        assertEquals(100, user.getBigPoints());
    }

    @Test
    void testGetStatsUser_returnsStats() {
        User user = new User("user4", "email4@test.com", "pass");
        user.setGamesPlayed(10);
        user.setGamesWon(5);
        user.setTotalPoints(200);
        user.setBigPoints(80);
        when(userRepository.findByUsername("user4")).thenReturn(Optional.of(user));
        UserStatistics stats = userService.getStatsUser("user4");
        assertEquals(10, stats.getGamesPlayed());
        assertEquals(5, stats.getGamesWon());
        assertEquals(200, stats.getTotalPoints());
        assertEquals(80, stats.getBigPoints());
        assertTrue(stats.getRatioWin() > 0);
    }
} 