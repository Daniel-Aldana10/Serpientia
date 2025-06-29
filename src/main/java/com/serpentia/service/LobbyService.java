package com.serpentia.service;


import com.serpentia.dto.CreateRoomRequest;
import com.serpentia.dto.RoomDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.serpentia.websocket.RoomEvent;

@Service
public class LobbyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String ROOM_KEY_PREFIX = "room:";

    public LobbyService(RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

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



    public RoomDTO getRoom(String roomId) {
        Object roomObj = redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + roomId);
        return roomObj != null ? (RoomDTO) roomObj : null;
    }

    public List<RoomDTO> getAllRooms() {
        Set<String> keys = redisTemplate.keys(ROOM_KEY_PREFIX + "*");
        if (keys == null) return new ArrayList<>();
        return keys.stream()
                .map(k -> (RoomDTO) redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void deleteRoom(String username, String roomId) {
        if(getRoom(roomId).getHost().equals(username)) {
            redisTemplate.delete(ROOM_KEY_PREFIX + roomId);
        }

    }

    public void addPlayerToRoom(String roomId, String player) {
        System.out.print("id " + roomId + " player " + player );
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
    public void deletePlayerToRoom(String roomId, String player) {
        RoomDTO room = getRoom(roomId);
        System.out.println("se quiere eliminar esta sala " + room);
        if(room != null && room.getCurrentPlayers().contains(player)) {
            room.getCurrentPlayers().remove(player);
            if(room.getHost().equals(player) && room.getCurrentPlayers().size() >= 1){
                room.setHost(room.getCurrentPlayers().get(0));
            } else if (room.getCurrentPlayers().isEmpty()) {
                deleteRoom(room.getHost(), roomId);
                messagingTemplate.convertAndSend("/topic/lobby", new RoomEvent("DELETED", room));
                return;
            }
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