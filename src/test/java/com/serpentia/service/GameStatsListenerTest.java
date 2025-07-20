package com.serpentia.service;

import com.serpentia.websocket.GameFinishedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class GameStatsListenerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private GameStatsListener gameStatsListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameStatsListener = new GameStatsListener(userService);
    }

    @Test
    void testHandleGameFinished_updatesUserStats() {
        GameFinishedEvent.PlayerResult result1 = new GameFinishedEvent.PlayerResult("user1", 100, 1, true);
        GameFinishedEvent.PlayerResult result2 = new GameFinishedEvent.PlayerResult("user2", 50, 2, false);
        GameFinishedEvent event = new GameFinishedEvent("room1", List.of(result1, result2));
        gameStatsListener.handleGameFinished(event);
        verify(userService).updateUserStats("user1", 100, true);
        verify(userService).updateUserStats("user2", 50, false);
    }
} 