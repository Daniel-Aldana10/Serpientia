package com.serpentia.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GameEventSubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(GameEventSubscriber.class);
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public GameEventSubscriber(ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            JsonNode root = objectMapper.readTree(json);

            // Si el mensaje tiene wrapper con metadata
            if (root.has("eventType") && root.has("payload")) {
                String eventType = root.get("eventType").asText();
                JsonNode payload = root.get("payload");

                handleEventByType(eventType, payload);
            } else {
                // Fallback para eventos sin wrapper (compatibilidad)
                handleLegacyEvent(root);
            }

        } catch (Exception e) {
            logger.error("Error procesando mensaje Redis: {}", e.getMessage(), e);
        }
    }

    private void handleEventByType(String eventType, JsonNode payload) {
        try {
            switch (eventType) {
                case "GameEvent" -> {
                    GameEvent event = objectMapper.treeToValue(payload, GameEvent.class);
                    String roomId = extractRoomId(event);
                    if (roomId != null) {
                        messagingTemplate.convertAndSend("/topic/game/" + roomId, event);
                        logger.debug("GameEvent enviado a sala: {}", roomId);
                    }
                }
                case "ScoreEvent" -> {
                    ScoreEvent event = objectMapper.treeToValue(payload, ScoreEvent.class);
                    if (event.getRoomId() != null) {
                        messagingTemplate.convertAndSend("/topic/game/" + event.getRoomId(), event);
                        logger.debug("ScoreEvent enviado a sala: {}", event.getRoomId());
                    }
                }
                case "GameFinishedEvent" -> {
                    GameFinishedEvent event = objectMapper.treeToValue(payload, GameFinishedEvent.class);
                    if (event.getRoomId() != null) {
                        messagingTemplate.convertAndSend("/topic/game/" + event.getRoomId(), event);
                        logger.debug("GameFinishedEvent enviado a sala: {}", event.getRoomId());
                    }
                }
                case "PlayerEliminatedEvent" -> {
                    PlayerEliminatedEvent event = objectMapper.treeToValue(payload, PlayerEliminatedEvent.class);
                    if (event.getRoomId() != null) {
                        messagingTemplate.convertAndSend("/topic/game/" + event.getRoomId(), event);
                        logger.debug("PlayerEliminatedEvent enviado a sala: {}", event.getRoomId());
                    }
                }
                default -> logger.warn("Tipo de evento no reconocido: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error procesando evento tipo {}: {}", eventType, e.getMessage(), e);
        }
    }

    private void handleLegacyEvent(JsonNode root) {
        // Lógica existente para compatibilidad hacia atrás
        try {
            if (root.has("players") && root.has("roomId")) {
                ScoreEvent event = objectMapper.treeToValue(root, ScoreEvent.class);
                messagingTemplate.convertAndSend("/topic/game/" + event.getRoomId(), event);
            } else if (root.has("results") && root.has("roomId")) {
                GameFinishedEvent event = objectMapper.treeToValue(root, GameFinishedEvent.class);
                messagingTemplate.convertAndSend("/topic/game/" + event.getRoomId(), event);
            } else if (root.has("type") && root.has("board")) {
                GameEvent event = objectMapper.treeToValue(root, GameEvent.class);
                String roomId = extractRoomId(event);
                if (roomId != null) {
                    messagingTemplate.convertAndSend("/topic/game/" + roomId, event);
                }
            }
        } catch (Exception e) {
            logger.error("Error procesando evento legacy: {}", e.getMessage(), e);
        }
    }

    private String extractRoomId(GameEvent event) {
        return event.getBoard() != null ? event.getBoard().getRoomId() : null;
    }
}