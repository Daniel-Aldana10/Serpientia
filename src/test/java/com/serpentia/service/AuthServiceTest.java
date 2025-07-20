package com.serpentia.service;

import com.serpentia.dto.AuthRequest;
import com.serpentia.dto.RegisterRequest;
import com.serpentia.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private AuthenticationManagerBuilder authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authenticationManager, userService, passwordEncoder, jwtUtil);
    }

    @Test
    void testLogin_success() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user1");
        request.setPassword("pass");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user1", "pass");
        Authentication authentication = mock(Authentication.class);
        var manager = mock(org.springframework.security.authentication.AuthenticationManager.class);
        when(authenticationManager.getObject()).thenReturn(manager);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(authentication)).thenReturn("jwt-token");
        String result = authService.login(request);
        assertEquals("jwt-token", result);
    }

    @Test
    void testRegister_callsUserService() {
        RegisterRequest request = new RegisterRequest();
        authService.register(request);
        verify(userService).registerUser(request);
    }
} 