package com.serpentia.controller;

import com.serpentia.dto.CreateRoomRequest;
import com.serpentia.dto.RoomDTO;
import com.serpentia.service.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {
    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/rooms")
    public List<RoomDTO> getRooms() {System.out.println(lobbyService.getAllRooms());return lobbyService.getAllRooms();}
    @PostMapping("/rooms")
    public RoomDTO createRoom(@RequestBody RoomDTO room) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return lobbyService.saveRoom(room);
    }
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        lobbyService.addPlayerToRoom(roomId, username);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deletePlayerToRoom(roomId, username);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deleteRoom(username, roomId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/rooms")
    public void deleteRooms() {
        lobbyService.deleteAllRooms();
    }
}
