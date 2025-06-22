package com.serpentia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de autenticación")
public class AuthRequest {
    @Schema(description = "Nombre de usuario", example = "player123", required = true)
    private String username;
    
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;
}