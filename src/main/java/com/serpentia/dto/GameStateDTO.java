package com.serpentia.dto;

import com.serpentia.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.serpentia.model.Team;
import com.serpentia.enums.GameMode;

/**
 * DTO para transferir el estado completo del juego al frontend.
 * Incluye información de jugadores, frutas y estado general.
 */
@Data
@NoArgsConstructor
public class GameStateDTO {
    
    /**
     * Identificador de la sala
     */
    private String roomId;
    
    /**
     * Ancho del tablero
     */
    private int width;
    
    /**
     * Alto del tablero
     */
    private int height;
    
    /**
     * Lista de jugadores en el juego
     */
    private List<PlayerDTO> players;
    
    /**
     * Lista de posiciones de frutas
     */
    private List<Point> fruits;
    
    /**
     * Estado del juego (WAITING, IN_GAME, FINISHED)
     */
    private String status;
    
    /**
     * Modo de juego (COMPETITIVE, TEAM, COOPERATIVE)
     */
    private GameMode gameMode;
    
    /**
     * Lista de equipos en el juego (solo para modo TEAM)
     */
    private List<TeamDTO> teams;
    
    /**
     * Mapeo de jugadores a equipos (solo para modo TEAM)
     */
    private Map<String, String> playerToTeam;
    
    /**
     * Constructor que convierte un BoardState a GameStateDTO
     */
    public GameStateDTO(com.serpentia.BoardState boardState, Map<String, com.serpentia.model.Player> players) {
        this.roomId = boardState.getRoomId();
        this.width = boardState.getWidth();
        this.height = boardState.getHeight();
        this.fruits = boardState.getFruits();
        this.status = boardState.getStatus();
        this.gameMode = boardState.getGameMode();
        this.playerToTeam = boardState.getPlayerToTeam();
        
        // Convertir jugadores a DTOs
        this.players = players.values().stream()
                .map(PlayerDTO::new)
                .toList();
        
        // Convertir equipos a DTOs si es modo TEAM
        if (gameMode == GameMode.TEAM && boardState.getTeams() != null) {
            this.teams = boardState.getTeams().values().stream()
                .map(TeamDTO::new)
                .toList();
        }
    }
    
    /**
     * Constructor con parámetros básicos
     */
    public GameStateDTO(String roomId, int width, int height, List<PlayerDTO> players, List<Point> fruits, String status) {
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        this.players = players;
        this.fruits = fruits;
        this.status = status;
    }
} 