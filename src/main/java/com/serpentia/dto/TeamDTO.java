package com.serpentia.dto;

import com.serpentia.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para transferir información de equipos al frontend.
 * Incluye información del equipo, jugadores y estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    
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
     * Constructor que convierte un Team a TeamDTO
     */
    public TeamDTO(Team team) {
        this.teamId = team.getTeamId();
        this.playerIds = team.getPlayerIds();
        this.teamScore = team.getTeamScore();
        this.eliminated = team.isEliminated();
        this.teamColor = team.getTeamColor();
    }
} 