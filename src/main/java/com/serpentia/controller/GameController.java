package com.serpentia.controller;

import com.serpentia.BoardState;
import com.serpentia.dto.GameStateDTO;
import com.serpentia.dto.RoomDTO;
import com.serpentia.service.GameService;
import com.serpentia.service.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void start(@PathVariable String roomId) {
        RoomDTO room = lobbyService.getRoom(roomId);
        if (room == null) {
            return;
        }
        gameService.initRoom(roomId, room.getCurrentPlayers(), room.getGameMode());
    }
  
    @DeleteMapping("/rooms/games")
    public void deleteAllGames() {
        gameService.deleteAllGames();
    }
    
    @GetMapping("/state/{roomId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable String roomId) {
        BoardState board = gameService.getBoardState(roomId);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        GameStateDTO gameState = new GameStateDTO(board, board.getPlayers());
        return ResponseEntity.ok(gameState);
    }
}