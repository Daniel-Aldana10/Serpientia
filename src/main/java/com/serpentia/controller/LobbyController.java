package com.serpentia.controller;
import com.serpentia.dto.RoomDTO;
import com.serpentia.service.LobbyService;
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

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
@Tag(name = "Lobby", description = "Endpoints para gestión de salas de juego")
@SecurityRequirement(name = "Bearer Authentication")
public class LobbyController {
    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/rooms")
    @Operation(
        summary = "Obtener todas las salas",
        description = "Retorna la lista de todas las salas de juego disponibles"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de salas obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "[{\"roomId\": \"12s9\", \"host\": \"player123\", \"gameMode\": \"COMPETITIVE\", \"maxPlayers\": 4, \"currentPlayers\": [\"player123\"], \"isFull\": false, \"powerups\": true}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public List<RoomDTO> getRooms() {
        System.out.println(lobbyService.getAllRooms());
        return lobbyService.getAllRooms();
    }

    @PostMapping("/rooms")
    @Operation(
        summary = "Crear nueva sala",
        description = "Crea una nueva sala de juego con la configuración especificada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Sala creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"roomId\": \"12s9\", \"host\": \"player123\", \"gameMode\": \"COMPETITIVE\", \"maxPlayers\": 4, \"currentPlayers\": [\"player123\"], \"isFull\": false, \"powerups\": true}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de sala inválidos"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public RoomDTO createRoom(@RequestBody RoomDTO room) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return lobbyService.saveRoom(room);
    }

    @PostMapping("/rooms/{roomId}/join")
    @Operation(
        summary = "Unirse a una sala",
        description = "Une al usuario autenticado a una sala específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario unido exitosamente a la sala"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Sala no encontrada"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Sala llena o usuario ya en la sala"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        lobbyService.addPlayerToRoom(roomId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomId}/leave")
    @Operation(
        summary = "Salir de una sala",
        description = "Saca al usuario autenticado de una sala específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario salió exitosamente de la sala"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Sala no encontrada"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deletePlayerToRoom(roomId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomId}")
    @Operation(
        summary = "Eliminar sala",
        description = "Elimina una sala específica (solo el host puede eliminarla)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Sala eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado - Solo el host puede eliminar la sala"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Sala no encontrada"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token JWT requerido"
        )
    })
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        lobbyService.deleteRoom(username, roomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms")
    public void deleteRooms() {
        lobbyService.deleteAllRooms();
    }
}
