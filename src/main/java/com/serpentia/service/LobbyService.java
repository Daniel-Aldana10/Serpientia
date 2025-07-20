package com.serpentia.service;

import com.serpentia.dto.RoomDTO;
import com.serpentia.repository.LobbyRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

import com.serpentia.websocket.RoomEvent;
import com.serpentia.exeptions.SerpentiaException;

/**
 * Servicio que maneja toda la lógica de negocio relacionada con las salas de juego.
 *
 * <p>Este servicio utiliza Redis para almacenar las salas en tiempo real y proporciona
 * funcionalidades para crear, gestionar y eliminar salas de juego. También se encarga
 * de enviar notificaciones en tiempo real a través de WebSocket cuando ocurren cambios
 * en las salas.</p>
 *
 * <p>Las salas se almacenan en Redis con el prefijo "room:" para facilitar su gestión
 * y búsqueda.</p>
 */
@Service
public class LobbyService {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyRepository lobbyRepository;
    private final PresenceService presenceService;



    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param lobbyRepository Repositorio de las salas
     * @param messagingTemplate Template para envío de mensajes WebSocket
     */
    public LobbyService(LobbyRepository lobbyRepository, SimpMessagingTemplate messagingTemplate, PresenceService presenceService) {
        this.lobbyRepository = lobbyRepository;
        this.messagingTemplate = messagingTemplate;
        this.presenceService = presenceService;
    }

    /**
     * Guarda o actualiza una sala en Redis.
     * @param room Sala a guardar o actualizar
     * @return Sala guardada con datos actualizados
     */
    public RoomDTO saveRoom(RoomDTO room) {
        if (room.getCurrentPlayers() == null || room.getCurrentPlayers().isEmpty()) {
            RoomDTO existing = getRoom(room.getRoomId());
            if (existing != null && existing.getCurrentPlayers() != null) {
                room.setCurrentPlayers(existing.getCurrentPlayers());
            } else {
                List<String> players = new ArrayList<>();
                players.add(room.getHost());
                room.setCurrentPlayers(players);
                messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("CREATED", room));
                presenceService.setUserRoom(room.getHost(), room.getRoomId());
            }
        }
        lobbyRepository.saveRoom(room);
        return room;
    }

    /**
     * Obtiene una sala específica por su ID.
     *
     * @param roomId Identificador único de la sala
     * @return Sala encontrada o null si no existe
     */
    public RoomDTO getRoom(String roomId) {
        return lobbyRepository.getRoom(roomId);
    }

    /**
     * Obtiene todas las salas disponibles en el sistema.
     *
     * @return Lista de todas las salas activas
     */
    public List<RoomDTO> getAllRooms() {
        return lobbyRepository.getAllRooms();
    }

    /**
     * Elimina una sala específica.
     * Lanza SerpentiaException si la sala no existe o el usuario no es el host.
     * @param username Nombre del usuario que intenta eliminar la sala
     * @param roomId Identificador de la sala a eliminar
     */
    public void deleteRoom(String username, String roomId) {
        RoomDTO room = getRoom(roomId);
        if (room == null) {
            throw new SerpentiaException("Sala no encontrada", "¡Ups! No se encontró la sala.", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        if (!room.getHost().equals(username)) {
            throw new SerpentiaException("Solo el host puede eliminar la sala", "No tienes permisos para eliminar la sala.", org.springframework.http.HttpStatus.FORBIDDEN);
        }
        lobbyRepository.deleteRoom(roomId);
    }

    /**
     * Añade un jugador a una sala específica.
     * Lanza SerpentiaException si la sala no existe, está llena o el usuario ya está en la sala.
     * @param roomId Identificador de la sala
     * @param player Nombre del jugador a añadir
     */
    public void addPlayerToRoom(String roomId, String player) {
        RoomDTO room = getRoom(roomId);
        if (room == null) {
            throw new SerpentiaException("Sala no encontrada", "¡Ups! No se encontró la sala.", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        if (room.getCurrentPlayers().contains(player)) {
            throw new SerpentiaException("El usuario ya está en la sala", "Ya te encuentras en la sala.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        if (room.isFull() || room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            throw new SerpentiaException("La sala está llena", "La sala ya no tiene cupo disponible.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        room.getCurrentPlayers().add(player);
        if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            room.setFull(true);
        }
        messagingTemplate.convertAndSend("/topic/lobby", new com.serpentia.websocket.RoomEvent("UPDATED", room));
        lobbyRepository.saveRoom(room);
        // Guardar relación usuario-sala en Redis
        presenceService.setUserRoom(player, roomId);
    }

    /**
     * Elimina un jugador de una sala específica.
     * Lanza SerpentiaException si la sala no existe o el usuario no está en la sala.
     * @param roomId Identificador de la sala
     * @param player Nombre del jugador a eliminar
     */
    public void deletePlayerToRoom(String roomId, String player) {
        RoomDTO room = getRoom(roomId);
        if (room == null) {
            throw new SerpentiaException("Sala no encontrada", "¡Ups! No se encontró la sala.", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        if (!room.getCurrentPlayers().contains(player)) {
            throw new SerpentiaException("El usuario no está en la sala", "No formas parte de la sala.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        room.getCurrentPlayers().remove(player);

        // Transferir host si el host sale y hay otros jugadores
        if(room.getHost().equals(player) && room.getCurrentPlayers().size() >= 1){
            room.setHost(room.getCurrentPlayers().get(0));
        } else if (room.getCurrentPlayers().isEmpty()) {
            // Eliminar sala si queda vacía
            deleteRoom(room.getHost(), roomId);
            messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("DELETED", room));
            // Eliminar relación usuario-sala en Redis
            presenceService.removeUserRoom(player);
            return;
        }

        // Actualizar estado de sala llena
        if (room.getCurrentPlayers().size() < room.getMaxPlayers()) {
            room.setFull(false);
        }
        lobbyRepository.saveRoom(room);
        messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("UPDATED", room));
        // Eliminar relación usuario-sala en Redis
        presenceService.removeUserRoom(player);
    }

    public void deleteAllRooms() {
        if(lobbyRepository.deleteAllRooms()) {
            messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("CLEARED", null));
        }
    }
}