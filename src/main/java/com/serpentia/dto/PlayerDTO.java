package com.serpentia.dto;

import com.serpentia.Point;
import com.serpentia.model.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO para transferir información del jugador al frontend.
 * Compatible con la interfaz Player del frontend.
 */
@Data
@NoArgsConstructor
public class PlayerDTO {
    
    /**
     * Nombre del jugador
     */
    private String name;
    
    /**
     * Color de la serpiente
     */
    private String color;
    
    /**
     * Posiciones de la serpiente (convertidas a List para el frontend)
     */
    private List<Point> snake;
    
    /**
     * Dirección actual del movimiento
     */
    private String direction;
    
    /**
     * Puntaje actual en la partida
     */
    private int score;
    
    /**
     * Indica si el jugador está vivo
     */
    private boolean alive;
    
    /**
     * Constructor que convierte un Player a PlayerDTO
     */
    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.color = player.getColor();
        this.direction = player.getDirection();
        this.score = player.getScore();
        this.alive = player.isAlive();
        
        // Convertir Deque a List para el frontend
        this.snake = new ArrayList<>(player.getSnake());
    }

} 