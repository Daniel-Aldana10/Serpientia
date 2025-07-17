package com.serpentia.repository;


import com.serpentia.BoardState;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class GameRepository {

    private final RedisTemplate<String, Object> redis;
    private static final String PREFIX = "game:";

    public GameRepository(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    public void saveBoard(BoardState board) {
        redis.opsForValue().set(PREFIX + board.getRoomId(), board);
    }

    public BoardState getBoard(String roomId) {
        Object obj = redis.opsForValue().get(PREFIX + roomId);
        return obj != null ? (BoardState) obj : null;
    }

    public Set<String> getAllGameKeys() {
        return redis.keys(PREFIX + "*");
    }

    public void deleteBoard(String roomId) {
        redis.delete(PREFIX + roomId);
    }
    public void deleteAllGames(){
        Set<String> keys = redis.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }

}