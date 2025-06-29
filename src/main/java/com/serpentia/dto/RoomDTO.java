package com.serpentia.dto;

import com.serpentia.enums.GameMode;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Información de una sala de juego")
public class RoomDTO implements Serializable {
    @Schema(description = "Identificador de la sala", example = "12s9")
    private String roomId;
    @Schema(description = "Nombre del host de la sala", example = "player123")
    private String host;
    @Schema(description = "Modo de juego de la sala", example = "COMPETITIVE")
    private GameMode gameMode;
    @Schema(description = "Número máximo de jugadores", example = "4")
    private int maxPlayers;
    @Schema(description = "Lista de jugadores actuales en la sala")
    private List<String> currentPlayers;
    @Schema(description = "Indica si la sala está llena", example = "false")
    private boolean isFull;
    @Schema(description = "Indica si los powerups están habilitados", example = "true")
    private boolean powerups;
}
