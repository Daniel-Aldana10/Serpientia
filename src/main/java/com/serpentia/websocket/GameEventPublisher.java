package com.serpentia.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(GameEventPublisher.class);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CHANNEL = "game-events";

    public GameEventPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica cualquier tipo de evento del juego a Redis Pub/Sub
     * @param event Evento a publicar (GameEvent, ScoreEvent, GameFinishedEvent, etc.)
     */
    public void publishEvent(Object event) {
        try {
            EventWrapper wrapper = new EventWrapper(
                    event.getClass().getSimpleName(),
                    System.currentTimeMillis(),
                    event
            );

            String json = objectMapper.writeValueAsString(wrapper);
            redisTemplate.convertAndSend(CHANNEL, json);

            logger.debug("Evento publicado: {} para canal {}",
                    event.getClass().getSimpleName(), CHANNEL);

        } catch (Exception e) {
            logger.error("Error al publicar evento: {}", e.getMessage(), e);
            // En producción podrías implementar retry logic o dead letter queue
        }
    }

    /**
     * Publica un GameEvent específico (método de conveniencia)
     */
    public void publish(GameEvent event) {
        publishEvent(event);
    }

    /**
     * Wrapper para agregar metadata a los eventos
     */
    private static class EventWrapper {
        private String eventType;
        private long timestamp;
        private Object payload;

        public EventWrapper(String eventType, long timestamp, Object payload) {
            this.eventType = eventType;
            this.timestamp = timestamp;
            this.payload = payload;
        }

        // Getters necesarios para Jackson
        public String getEventType() { return eventType; }
        public long getTimestamp() { return timestamp; }
        public Object getPayload() { return payload; }
    }
}