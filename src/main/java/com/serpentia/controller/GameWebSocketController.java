package com.serpentia.controller;

import com.serpentia.service.GameService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


import java.util.Map;

@Controller
public class GameWebSocketController {
    private final GameService gameService;

    public GameWebSocketController(GameService gs) {
        this.gameService = gs;
    }
    @MessageMapping("/room/{roomId}/move")
    public void onMove(@DestinationVariable String roomId,
                       @Payload Map<String, String> payload) {
        String player = payload.get("player");
        String dir = payload.get("direction");
        System.out.println("Recibido movimiento: " + player + " -> " + dir + " en sala " + roomId);


        gameService.setDirection(roomId, player, dir);
    }

}