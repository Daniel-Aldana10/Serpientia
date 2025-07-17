package com.serpentia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase que representa un equipo en el juego.
 * Maneja el estado del equipo incluyendo jugadores, puntuación y estado de eliminación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
    
    /**
     * ID único del equipo
     */
    private String teamId;
    
    /**
     * Lista de IDs de jugadores que pertenecen al equipo
     */
    private List<String> playerIds;
    
    /**
     * Puntaje total del equipo
     */
    private int teamScore;
    
    /**
     * Indica si el equipo está eliminado
     */
    private boolean eliminated;
    
    /**
     * Color del equipo para identificación visual
     */
    private String teamColor;
    
    /**
     * Constructor principal para crear un equipo
     */
    public Team(String teamId, List<String> playerIds) {
        this.teamId = teamId;
        this.playerIds = playerIds;
        this.teamScore = 0;
        this.eliminated = false;
        this.teamColor = teamId.equals("team1") ? "#FF0000" : "#0000FF"; // Rojo para equipo 1, Azul para equipo 2
    }
    
    /**
     * Agrega puntos al equipo
     * @param points Puntos a agregar
     */
    public void addScore(int points) {
        this.teamScore += points;
    }
    
    /**
     * Marca al equipo como eliminado
     */
    public void eliminate() {
        this.eliminated = true;
    }
    
    /**
     * Verifica si el equipo tiene jugadores vivos
     * @param alivePlayers Lista de jugadores vivos
     * @return true si al menos un jugador del equipo está vivo
     */
    public boolean hasAlivePlayers(List<String> alivePlayers) {
        return playerIds.stream().anyMatch(alivePlayers::contains);
    }
} 