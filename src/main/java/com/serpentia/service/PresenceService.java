package com.serpentia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {


    private final StringRedisTemplate redisTemplate;

    private static final String USER_ROOM_PREFIX = "user_room:";

    public PresenceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Asocia un usuario a una sala en Redis.
     * @param username Nombre de usuario
     * @param roomId ID de la sala
     */
    public void setUserRoom(String username, String roomId) {
        redisTemplate.opsForValue().set(USER_ROOM_PREFIX + username, roomId);
    }

    /**
     * Obtiene la sala asociada a un usuario.
     * @param username Nombre de usuario
     * @return ID de la sala o null si no está asociado
     */
    public String getUserRoom(String username) {
        return redisTemplate.opsForValue().get(USER_ROOM_PREFIX + username);
    }

    /**
     * Elimina la asociación usuario-sala en Redis.
     * @param username Nombre de usuario
     */
    public void removeUserRoom(String username) {
        redisTemplate.delete(USER_ROOM_PREFIX + username);
    }
} 