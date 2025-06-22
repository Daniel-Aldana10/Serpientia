package com.serpentia.controller;

import com.serpentia.dto.UserStatistics;
import com.serpentia.model.User;
import com.serpentia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuario", description = "Endpoints para gestión de perfil de usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(
        summary = "Obtener perfil del usuario",
        description = "Retorna las estadísticas y datos del perfil del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Perfil obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"username\": \"player123\", \"gamesPlayed\": 10, \"gamesWon\": 5, \"winRate\": 50.0}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
        )
    })
    public ResponseEntity<UserStatistics> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            UserStatistics profile = userService.getStatsUser(username);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/profile")
    @Operation(
        summary = "Actualizar perfil del usuario",
        description = "Permite actualizar información del perfil del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Perfil actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public ResponseEntity<String> updateProfile(@RequestBody UserStatistics updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Aquí implementarías la lógica para actualizar el perfil
            return ResponseEntity.ok("Perfil actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar perfil: " + e.getMessage());
        }
    }
}
