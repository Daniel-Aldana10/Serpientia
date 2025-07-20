package com.serpentia.service;

import com.serpentia.websocket.GameFinishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener que procesa eventos de juego para actualizar estadísticas de usuarios.
 * Se encarga de mantener las estadísticas actualizadas cuando ocurren eventos
 * como eliminación de jugadores o fin de partida.
 */
@Component
public class GameStatsListener {

    private final UserService userService;

    public GameStatsListener(UserService userService) {
        this.userService = userService;
    }

    /*
     * Maneja el evento cuando termina el juego.
     * Procesa todos los resultados y actualiza las estadísticas de todos los jugadores.
     * 
     * @param event Evento de fin de juego
     */
    @EventListener
    public void handleGameFinished(GameFinishedEvent event) {
        System.out.println("Juego terminado en sala: " + event.getRoomId());
        

        for (GameFinishedEvent.PlayerResult result : event.getResults()) {
            
            userService.updateUserStats(
                result.getUsername(),
                result.getFinalScore(),
                result.isWon()
            );
        }
    }
} 