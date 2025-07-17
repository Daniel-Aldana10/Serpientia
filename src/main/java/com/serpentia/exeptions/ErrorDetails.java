package com.serpentia.exeptions;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO para estructurar la respuesta de error en la API.
 * Incluye mensaje técnico, mensaje especial para el usuario y la fecha/hora del error.
 */
@Data
@Builder
public class ErrorDetails {
    /** Mensaje técnico del error */
    private String message;
    /** Mensaje amigable para el usuario */
    private String mensajeEspecial;
    /** Fecha y hora del error */
    private LocalDateTime date;
} 