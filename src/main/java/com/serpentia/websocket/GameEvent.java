package com.serpentia.websocket;

import com.serpentia.model.BoardState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEvent {
    private String type; // "COLLISION", "FRUIT", "START", "END", "UPDATE", etc.
    private String player; // jugador afectado (opcional)
    private BoardState board; // estado actual del tablero
} 