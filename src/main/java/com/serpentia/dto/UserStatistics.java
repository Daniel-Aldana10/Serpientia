package com.serpentia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Esta clase contiene toda la información estadística relacionada con el
 * rendimiento de un usuario en el juego, incluyendo partidas jugadas, ganadas,
 * puntuaciones y ratios calculados automáticamente.<
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estadísticas de un usuario")
public class UserStatistics {
    @Schema(description = "Número total de partidas jugadas", example = "25")
    private Integer gamesPlayed;
    @Schema(description = "Número de partidas ganadas", example = "15")
    private Integer gamesWon;
    @Schema(description = "Puntos totales acumulados", example = "1250")
    private Integer totalPoints;
    @Schema(description = "Puntos de partidas grandes", example = "500")
    private Integer bigPoints;
    @Schema(description = "Ratio de victorias (porcentaje)", example = "60.0")
    private float ratioWin;
}
