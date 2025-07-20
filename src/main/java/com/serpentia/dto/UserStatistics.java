package com.serpentia.dto;

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
public class UserStatistics {
    private Integer gamesPlayed;
    private Integer gamesWon;
    private Integer totalPoints;
    private Integer bigPoints;
    private float ratioWin;
}
