package com.serpentia.websocket;

/**
 * Evento que se publica cuando un jugador es eliminado del juego.
 * Contiene información sobre el jugador eliminado y su rendimiento en la partida.
 */
public class PlayerEliminatedEvent {
    private String username;
    private String roomId;
    private int finalScore;
    private int position; // Posición final (1er, 2do, etc.)

    public PlayerEliminatedEvent(String username, String roomId, int finalScore, int position) {
        this.username = username;
        this.roomId = roomId;
        this.finalScore = finalScore;
        this.position = position;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getPosition() {
        return position;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public void setPosition(int position) {
        this.position = position;
    }
} 