package com.serpentia.websocket;

import com.serpentia.dto.RoomDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomEvent {
    private String type; // e.g. "CREATED", "FINISHED"
    private RoomDTO room;

    public RoomEvent(String type, RoomDTO room) {
        this.type = type;
        this.room = room;
    }
}

