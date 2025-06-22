package com.serpentia.dto;

import com.serpentia.enums.GameMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud para crear una nueva sala de juego")
public class CreateRoomRequest {
    @Schema(description = "Modo de juego de la sala", example = "COMPETITIVE", required = true)
    private GameMode gameMode;
    
    @Schema(description = "Número máximo de jugadores", example = "4", required = true, minimum = "2", maximum = "4")
    private int maxPlayers;
    
    @Schema(description = "Puntuación objetivo para ganar", example = "100", required = true, minimum = "10")
    private int targetScore;
    
    @Schema(description = "Habilitar powerups en el juego", example = "true", defaultValue = "false")
    private boolean powerups;
}
