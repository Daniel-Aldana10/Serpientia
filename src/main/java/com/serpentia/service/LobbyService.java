package com.serpentia.service;

import com.serpentia.dto.CreateRoomRequest;
import com.serpentia.dto.RoomDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.serpentia.websocket.RoomEvent;

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

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Prefijo utilizado para las claves de salas en Redis.
     */
    private static final String ROOM_KEY_PREFIX = "room:";

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param redisTemplate Template de Redis para operaciones de cache
     * @param messagingTemplate Template para envío de mensajes WebSocket
     */
    public LobbyService(RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
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
            }
        }

        redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getRoomId(), room);
        return room;
    }

    /**
     * Obtiene una sala específica por su ID.
     * 
     * @param roomId Identificador único de la sala
     * @return Sala encontrada o null si no existe
     */
    public RoomDTO getRoom(String roomId) {
        Object roomObj = redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + roomId);
        return roomObj != null ? (RoomDTO) roomObj : null;
    }

    /**
     * Obtiene todas las salas disponibles en el sistema.
     * 
     * @return Lista de todas las salas activas
     */
    public List<RoomDTO> getAllRooms() {
        Set<String> keys = redisTemplate.keys(ROOM_KEY_PREFIX + "*");
        if (keys == null) return new ArrayList<>();
        return keys.stream()
                .map(k -> (RoomDTO) redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una sala específica.
     *
     * @param username Nombre del usuario que intenta eliminar la sala
     * @param roomId Identificador de la sala a eliminar
     */
    public void deleteRoom(String username, String roomId) {
        RoomDTO room = getRoom(roomId);
        if (room != null && room.getHost().equals(username)) {
            redisTemplate.delete(ROOM_KEY_PREFIX + roomId);
        }
    }

    /**
     * Añade un jugador a una sala específica.
     * 
     * Este método valida que la sala exista y que el jugador no esté ya en ella.
     * Si la sala se llena después de añadir al jugador, se marca como llena.
     * Se envía una notificación de actualización a todos los clientes conectados.
     * 
     * @param roomId Identificador de la sala
     * @param player Nombre del jugador a añadir
     */
    public void addPlayerToRoom(String roomId, String player) {
        System.out.print("id " + roomId + " player " + player);
        RoomDTO room = getRoom(roomId);
        if (room != null && !room.getCurrentPlayers().contains(player)) {
            room.getCurrentPlayers().add(player);
            if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
                room.setFull(true);
            }
            System.out.println(room);
            saveRoom(room);
            messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("UPDATED", room));
        }
    }

    /**
     * Elimina un jugador de una sala específica.
     * @param roomId Identificador de la sala
     * @param player Nombre del jugador a eliminar
     */
    public void deletePlayerToRoom(String roomId, String player) {
        RoomDTO room = getRoom(roomId);
        System.out.println("se quiere eliminar esta sala " + room);
        if(room != null && room.getCurrentPlayers().contains(player)) {
            room.getCurrentPlayers().remove(player);
            
            // Transferir host si el host sale y hay otros jugadores
            if(room.getHost().equals(player) && room.getCurrentPlayers().size() >= 1){
                room.setHost(room.getCurrentPlayers().get(0));
            } else if (room.getCurrentPlayers().isEmpty()) {
                // Eliminar sala si queda vacía
                deleteRoom(room.getHost(), roomId);
                messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("DELETED", room));
                return;
            }
            
            // Actualizar estado de sala llena
            if (room.getCurrentPlayers().size() < room.getMaxPlayers()) {
                room.setFull(false);
            }
            
            System.out.println("Jugador eliminado: " + player + " de sala " + roomId);
            saveRoom(room);
            messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("UPDATED", room));
        }
    }

    public void deleteAllRooms() {
        Set<String> keys = redisTemplate.keys(ROOM_KEY_PREFIX + "*");
        System.out.println("Claves existentes: " + keys);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("CLEARED", null));
        }
    }
}