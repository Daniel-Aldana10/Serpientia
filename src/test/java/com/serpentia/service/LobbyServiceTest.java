package com.serpentia.service;

import com.serpentia.dto.RoomDTO;
import com.serpentia.repository.LobbyRepository;
import com.serpentia.websocket.RoomEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LobbyServiceTest {
    @Mock
    private LobbyRepository lobbyRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private PresenceService presenceService;
    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lobbyService = new LobbyService(lobbyRepository, messagingTemplate, presenceService);
    }

    @Test
    void testGetRoom_returnsRoom() {
        RoomDTO room = new RoomDTO();
        when(lobbyRepository.getRoom("room1")).thenReturn(room);
        assertEquals(room, lobbyService.getRoom("room1"));
    }

    @Test
    void testGetAllRooms_returnsList() {
        List<RoomDTO> rooms = new ArrayList<>();
        when(lobbyRepository.getAllRooms()).thenReturn(rooms);
        assertEquals(rooms, lobbyService.getAllRooms());
    }

    @Test
    void testDeleteAllRooms_clearsRooms() {
        when(lobbyRepository.deleteAllRooms()).thenReturn(true);
        lobbyService.deleteAllRooms();
        verify(messagingTemplate).convertAndSend(contains("/topic/lobby"), any(RoomEvent.class));
    }
} 