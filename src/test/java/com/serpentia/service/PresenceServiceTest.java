package com.serpentia.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PresenceServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private PresenceService presenceService;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        presenceService = new PresenceService(redisTemplate);
    }

    @Test
    void testSetUserRoom() {
        presenceService.setUserRoom("daniel", "room1");

        verify(valueOperations).set("user_room:daniel", "room1");
    }

    @Test
    void testGetUserRoom() {
        when(valueOperations.get("user_room:daniel")).thenReturn("room1");

        String room = presenceService.getUserRoom("daniel");

        assertEquals("room1", room);
        verify(valueOperations).get("user_room:daniel");
    }

    @Test
    void testRemoveUserRoom() {
        presenceService.removeUserRoom("daniel");

        verify(redisTemplate).delete("user_room:daniel");
    }
}
