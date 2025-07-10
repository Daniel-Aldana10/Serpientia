package com.serpentia.controller;

import com.serpentia.dto.RoomDTO;
import com.serpentia.service.GameService;
import com.serpentia.service.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

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
    @Operation(summary = "Iniciar juego", description = "Inicia una partida en la sala especificada por roomId.")
    public ResponseEntity<?> start(@PathVariable String roomId) {
        RoomDTO room = lobbyService.getRoom(roomId);
        gameService.initRoom(roomId, room.getCurrentPlayers());
        return ResponseEntity.ok().build();
    }
    /*
    // ✅ CORREGIDO: Usar @DestinationVariable en lugar de @PathVariable
    @MessageMapping("/room/{roomId}/move")
    public void onMove(@DestinationVariable String roomId,
                       @Payload Map<String, String> payload) {
        String player = payload.get("player");
        String dir = payload.get("direction");
        System.out.println("Recibido movimiento: " + player + " -> " + dir + " en sala " + roomId);

        gameService.setDirection(roomId, player, dir);
    }
    */
    @DeleteMapping("/rooms/games")
    public void deleteAllGames() {
        gameService.deleteAllGames();
    }
}