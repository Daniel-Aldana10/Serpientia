package com.serpentia.controller;
import com.serpentia.dto.RoomDTO;
import com.serpentia.service.LobbyService;
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
    public List<RoomDTO> getRooms() {
        return lobbyService.getAllRooms();
    }

    @PostMapping("/rooms")
    public RoomDTO createRoom(@RequestBody RoomDTO room) {
        return lobbyService.saveRoom(room);
    }

    @PostMapping("/rooms/{roomId}/join")
    public void joinRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.addPlayerToRoom(roomId, username);
    }

    @DeleteMapping("/rooms/{roomId}/leave")
    public void leaveRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deletePlayerToRoom(roomId, username);

    }

    @DeleteMapping("/rooms/{roomId}")
    public void deleteRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deleteRoom(username, roomId);
    }

    @DeleteMapping("/rooms")
    public void deleteRooms() {
        lobbyService.deleteAllRooms();
    }
}
