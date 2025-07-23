package com.serpentia.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Escucha eventos del juego publicados localmente y los reenvía a Redis
 * para distribuirlos a otros nodos del backend.
 *
 * Este componente actúa como un "relay" que toma eventos generados por
 * la lógica de negocio local y los propaga a través de Redis Pub/Sub
 * para que lleguen a todas las instancias del backend.
 */
@Component
public class GameEventRelayListener {

    private static final Logger logger = LoggerFactory.getLogger(GameEventRelayListener.class);
    private final GameEventPublisher publisher;

    public GameEventRelayListener(GameEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Reenvía GameEvent a Redis para distribución multi-nodo
     */
    @EventListener
    public void onGameEvent(GameEvent event) {
        try {
            publisher.publishEvent(event);
            logger.debug("GameEvent relay: {} para room {}",
                    event.getType(),
                    event.getBoard() != null ? event.getBoard().getRoomId() : "unknown");
        } catch (Exception e) {
            logger.error("Error reenviando GameEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Reenvía ScoreEvent a Redis para distribución multi-nodo
     */
    @EventListener
    public void onScoreEvent(ScoreEvent event) {
        try {
            publisher.publishEvent(event);
            logger.debug("ScoreEvent relay: {} para room {}",
                    event.getType(), event.getRoomId());
        } catch (Exception e) {
            logger.error("Error reenviando ScoreEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Reenvía GameFinishedEvent a Redis para distribución multi-nodo
     */
    @EventListener
    public void onGameFinishedEvent(GameFinishedEvent event) {
        try {
            publisher.publishEvent(event);
            logger.debug("GameFinishedEvent relay para room {}", event.getRoomId());
        } catch (Exception e) {
            logger.error("Error reenviando GameFinishedEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Reenvía PlayerEliminatedEvent a Redis para distribución multi-nodo
     */
    @EventListener
    public void onPlayerEliminatedEvent(PlayerEliminatedEvent event) {
        try {
            publisher.publishEvent(event);
            logger.debug("PlayerEliminatedEvent relay: {} eliminado de room {}",
                    event.getUsername(), event.getRoomId());
        } catch (Exception e) {
            logger.error("Error reenviando PlayerEliminatedEvent: {}", e.getMessage(), e);
        }
    }
}