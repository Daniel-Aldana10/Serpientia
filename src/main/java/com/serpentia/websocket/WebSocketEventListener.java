package com.serpentia.websocket;

import com.serpentia.service.LobbyService;
import com.serpentia.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {


    private final LobbyService lobbyService;

    private final PresenceService presenceService;

    public WebSocketEventListener(LobbyService lobbyService, PresenceService presenceService) {
        this.lobbyService = lobbyService;
        this.presenceService = presenceService;
    }

    /**
     * Este listener se ejecuta cuando un usuario se desconecta del WebSocket.
     * Si la sesión tiene un roomId, lo elimina de la sala correspondiente.
     * Si no hay roomId (por ejemplo, solo estaba viendo el lobby), no hace nada.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            String roomId = presenceService.getUserRoom(username);
            if (roomId != null) {
                lobbyService.deletePlayerToRoom(roomId, username);
                presenceService.removeUserRoom(username);
                System.out.println("Usuario " + username + " eliminado de la sala " + roomId + " por desconexión.");
            } else {
                System.out.println("Desconexión detectada pero el usuario no estaba en ninguna sala: username=" + username);
            }
        } else {
            System.out.println("Desconexión detectada pero falta el username en la sesión WebSocket");
        }
    }
} 