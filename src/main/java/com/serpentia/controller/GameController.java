package com.serpentia.controller;

import com.serpentia.BoardState;
import com.serpentia.dto.GameStateDTO;
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
  
    @DeleteMapping("/rooms/games")
    public void deleteAllGames() {
        gameService.deleteAllGames();
    }
    
    @GetMapping("/state/{roomId}")
    @Operation(
        summary = "Obtener estado del juego",
        description = "Retorna el estado actual del juego incluyendo jugadores y puntajes"
    )
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable String roomId) {
        BoardState board = gameService.getBoardState(roomId);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(board.getPlayers() +"no ignorar porfavor");
        GameStateDTO gameState = new GameStateDTO(board, board.getPlayers());
        return ResponseEntity.ok(gameState);
    }
}