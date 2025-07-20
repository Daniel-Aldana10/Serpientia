package com.serpentia.service;

import com.serpentia.BoardState;
import com.serpentia.enums.GameMode;
import com.serpentia.model.Player;
import com.serpentia.repository.GameRepository;
import com.serpentia.websocket.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private SimpMessagingTemplate ws;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameRepository, ws, eventPublisher);
    }

    @Test
    void testInitRoom_createsBoardAndSendsEvent() {
        String roomId = "room1";
        var players = Arrays.asList("player1", "player2");
        gameService.initRoom(roomId, players, GameMode.COMPETITIVE);
        verify(gameRepository, times(1)).saveBoard(any(BoardState.class));
        verify(ws, atLeastOnce()).convertAndSend(contains(roomId), any(GameEvent.class));
    }

    @Test
    void testSetDirection_updatesDirection() {
        String roomId = "room1";
        String player = "player1";
        BoardState board = mock(BoardState.class);
        when(gameRepository.getBoard(roomId)).thenReturn(board);
        when(board.getStatus()).thenReturn("IN_GAME");
        when(board.getSnakeDirections()).thenReturn(new java.util.HashMap<>());
        doNothing().when(board).reconstructPlayersIfNeeded();
        gameService.setDirection(roomId, player, "UP");
        verify(gameRepository).saveBoard(board);
    }

    @Test
    void testDeleteAllGames_callsRepository() {
        gameService.deleteAllGames();
        verify(gameRepository).deleteAllGames();
    }

    @Test
    void testGetBoardState_returnsBoard() {
        String roomId = "room1";
        BoardState board = new BoardState();
        when(gameRepository.getBoard(roomId)).thenReturn(board);
        BoardState result = gameService.getBoardState(roomId);
        assertEquals(board, result);
    }
} 