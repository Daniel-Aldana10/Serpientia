package com.serpentia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de registro de nuevo usuario")
public class RegisterRequest {
    @Schema(description = "Nombre de usuario único", example = "player123", required = true, minLength = 3, maxLength = 15)
    private String username;
    
    @Schema(description = "Email del usuario", example = "player@example.com", required = true, format = "email")
    private String email;
    
    @Schema(description = "Contraseña del usuario", example = "password123", required = true, minLength = 6)
    private String password;
} 