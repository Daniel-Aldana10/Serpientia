package com.serpentia.dto;

import com.serpentia.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Representa una sala de juego en el sistema.
 * Esta clase se utiliza para transferir información de salas entre el cliente
 * y el servidor, así como para almacenar las salas en Redis. Implementa Serializable
 * para permitir su almacenamiento en cache.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO implements Serializable {
    private String roomId;
    private String host;
    private GameMode gameMode;
    private int maxPlayers;
    private List<String> currentPlayers;
    private boolean isFull;
    private boolean powerups;
}
