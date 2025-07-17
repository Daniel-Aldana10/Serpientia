package com.serpentia.service;

import com.serpentia.websocket.PlayerEliminatedEvent;
import com.serpentia.websocket.GameFinishedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener que procesa eventos de juego para actualizar estadísticas de usuarios.
 * Se encarga de mantener las estadísticas actualizadas cuando ocurren eventos
 * como eliminación de jugadores o fin de partida.
 */
@Component
public class GameStatsListener {
    
    @Autowired
    private UserService userService;
    
    /**
     * Maneja el evento cuando un jugador es eliminado del juego.
     * Actualiza las estadísticas del jugador eliminado.
     * 
     * @param event Evento de eliminación del jugador
     */
    
    /**
     * Maneja el evento cuando termina el juego.
     * Procesa todos los resultados y actualiza las estadísticas de todos los jugadores.
     * 
     * @param event Evento de fin de juego
     */
    @EventListener
    public void handleGameFinished(GameFinishedEvent event) {
        System.out.println("Juego terminado en sala: " + event.getRoomId());
        
        // Procesar todos los resultados del juego
        for (GameFinishedEvent.PlayerResult result : event.getResults()) {
            System.out.println("Resultado - " + result.getUsername() + 
                             " : " + result.getFinalScore() + " puntos, " +
                         " posición " + result.getPosition() + 
                        ", ganó: " + result.isWon());
            
            userService.updateUserStats(
                result.getUsername(),
                result.getFinalScore(),
                result.isWon()
            );
        }
    }
} 