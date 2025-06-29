package com.serpentia.websocket;

import com.serpentia.dto.RoomDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa un evento relacionado con una sala de juego.
 */
@Getter
@Setter
public class RoomEvent {
    
    /**
     * Tipo de evento que ha ocurrido.
     */
    private String type;
    
    /**
     * Datos de la sala relacionada con el evento.
     */
    private RoomDTO room;

    /**
     * Constructor que crea un nuevo evento de sala.
     * @param type Tipo de evento (CREATED, UPDATED, DELETED, CLEARED)
     * @param room Datos de la sala relacionada con el evento
     */
    public RoomEvent(String type, RoomDTO room) {
        this.type = type;
        this.room = room;
    }
}

