package com.serpentia.dto;

import com.serpentia.enums.GameMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información de una sala de juego")
public class RoomDTO implements Serializable {
    private@Schema(description = "Identificador de la sala", example = "12s9")
    String roomId;
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
