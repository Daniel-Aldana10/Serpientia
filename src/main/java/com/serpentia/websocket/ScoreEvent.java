package com.serpentia.websocket;

import com.serpentia.dto.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Evento WebSocket para actualizaciones de puntajes.
 * Se envía cuando un jugador gana puntos o cuando hay cambios en el ranking.
 */
@Data
@NoArgsConstructor
public class ScoreEvent {
    
    /**
     * Tipo de evento de puntaje
     */
    private String type; // "SCORE_UPDATE", "RANKING_UPDATE", "GAME_END"
    /**
     * Lista actualizada de jugadores con sus puntajes
     */
    private List<PlayerDTO> players;
    
    /**
     * Constructor para evento de actualización de puntaje
     */
    public ScoreEvent(String type, java.util.List<PlayerDTO> players) {
        this.type = type;
        this.players = players;
    }

} 