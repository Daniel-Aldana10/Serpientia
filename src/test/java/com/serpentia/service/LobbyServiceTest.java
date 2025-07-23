package com.serpentia.service;

import com.serpentia.dto.RoomDTO;
import com.serpentia.exeptions.SerpentiaException;
import com.serpentia.repository.LobbyRepository;
import com.serpentia.websocket.RoomEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

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
    }

    @Test
    void testGetRoom_returnsRoom() {
        RoomDTO room = new RoomDTO();
        when(lobbyRepository.getRoom("room1")).thenReturn(room);

        RoomDTO result = lobbyService.getRoom("room1");

        assertEquals(room, result);
        verify(lobbyRepository).getRoom("room1");
    }

    @Test
    void testGetAllRooms_returnsList() {
        List<RoomDTO> rooms = new ArrayList<>();
        when(lobbyRepository.getAllRooms()).thenReturn(rooms);

        List<RoomDTO> result = lobbyService.getAllRooms();

        assertEquals(rooms, result);
        verify(lobbyRepository).getAllRooms();
    }

    @Test
    void testDeleteAllRooms_clearsRoomsAndSendsEvent() {
        when(lobbyRepository.deleteAllRooms()).thenReturn(true);

        lobbyService.deleteAllRooms();

        verify(lobbyRepository).deleteAllRooms();
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby"), any(RoomEvent.class));
    }

    @Test
    void testSaveRoom_createsNewRoomAndSavesIt() {
        RoomDTO room = new RoomDTO();
        room.setRoomId("room1");
        room.setHost("daniel");
        room.setMaxPlayers(4);

        when(lobbyRepository.getRoom("room1")).thenReturn(null);

        RoomDTO saved = lobbyService.saveRoom(room);

        assertNotNull(saved.getCurrentPlayers());
        assertTrue(saved.getCurrentPlayers().contains("daniel"));
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby"), any(RoomEvent.class));
        verify(presenceService).setUserRoom("daniel", "room1");
        verify(lobbyRepository).saveRoom(room);
    }

    @Test
    void testAddPlayerToRoom_successful() {
        RoomDTO room = new RoomDTO();
        room.setRoomId("room1");
        room.setHost("daniel");
        room.setMaxPlayers(4);
        room.setCurrentPlayers(new ArrayList<>(List.of("daniel")));

        when(lobbyRepository.getRoom("room1")).thenReturn(room);

        lobbyService.addPlayerToRoom("room1", "juan");

        assertTrue(room.getCurrentPlayers().contains("juan"));
        verify(presenceService).setUserRoom("juan", "room1");
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby"), any(RoomEvent.class));
        verify(lobbyRepository).saveRoom(room);
    }

    @Test
    void testAddPlayerToRoom_userAlreadyInRoom() {
        RoomDTO room = new RoomDTO();
        room.setRoomId("room1");
        room.setHost("daniel");
        room.setMaxPlayers(4);
        room.setCurrentPlayers(new ArrayList<>(List.of("daniel")));

        when(lobbyRepository.getRoom("room1")).thenReturn(room);

        SerpentiaException ex = assertThrows(SerpentiaException.class, () ->
                lobbyService.addPlayerToRoom("room1", "daniel")
        );

        assertEquals("El usuario ya está en la sala", ex.getMessage());
    }

    @Test
    void testAddPlayerToRoom_roomIsFull() {
        RoomDTO room = new RoomDTO();
        room.setRoomId("room1");
        room.setHost("daniel");
        room.setMaxPlayers(1);
        room.setFull(true);
        room.setCurrentPlayers(new ArrayList<>(List.of("daniel")));

        when(lobbyRepository.getRoom("room1")).thenReturn(room);

        SerpentiaException ex = assertThrows(SerpentiaException.class, () ->
                lobbyService.addPlayerToRoom("room1", "otro")
        );

        assertEquals("La sala está llena", ex.getMessage());
    }

    @Test
    void testDeleteRoom_userIsNotHost() {
        RoomDTO room = new RoomDTO();
        room.setRoomId("room1");
        room.setHost("daniel");

        when(lobbyRepository.getRoom("room1")).thenReturn(room);

        SerpentiaException ex = assertThrows(SerpentiaException.class, () ->
                lobbyService.deleteRoom("juan", "room1")
        );

        assertEquals("Solo el host puede eliminar la sala", ex.getMessage());
    }

    @Test
    void testDeleteRoom_roomNotFound() {
        when(lobbyRepository.getRoom("room1")).thenReturn(null);

        SerpentiaException ex = assertThrows(SerpentiaException.class, () ->
                lobbyService.deleteRoom("daniel", "room1")
        );

        assertEquals("Sala no encontrada", ex.getMessage());
    }
}
