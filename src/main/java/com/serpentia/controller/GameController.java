package com.serpentia.controller;

import com.serpentia.dto.RoomDTO;
import com.serpentia.service.GameService;
import com.serpentia.service.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final LobbyService lobbyService;

    public GameController(GameService gs, LobbyService ls) {
        this.gameService = gs;
        this.lobbyService = ls;
    }

    @PostMapping("/start/{roomId}")
    public ResponseEntity<?> start(@PathVariable String roomId) {
        RoomDTO room = lobbyService.getRoom(roomId);
        gameService.initRoom(roomId, room.getCurrentPlayers());
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/room/{roomId}/move")
    public void onMove(@PathVariable String roomId,
                       @Payload Map<String, String> payload) {
        String player = payload.get("player");
        String dir    = payload.get("direction");
        gameService.setDirection(roomId, player, dir);
    }
}
