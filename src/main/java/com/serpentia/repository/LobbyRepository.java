package com.serpentia.repository;

import com.serpentia.dto.RoomDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class LobbyRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ROOM_KEY_PREFIX = "room:";

    public LobbyRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRoom(RoomDTO room) {
        redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getRoomId(), room);
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

    public void deleteRoom(String roomId) {
        redisTemplate.delete(ROOM_KEY_PREFIX + roomId);
    }

    public boolean deleteAllRooms() {
        Set<String> keys = redisTemplate.keys(ROOM_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            return true;
        }
        return false;
    }
}
