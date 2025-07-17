package com.serpentia.websocket;

import java.util.List;

/**
 * Evento que se publica cuando termina el juego.
 * Contiene información de todos los jugadores con sus resultados finales.
 */
public class GameFinishedEvent {
    private String roomId;
    private List<PlayerResult> results;

    public GameFinishedEvent(String roomId, List<PlayerResult> results) {
        this.roomId = roomId;
        this.results = results;
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }

    public List<PlayerResult> getResults() {
        return results;
    }

    // Setters
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setResults(List<PlayerResult> results) {
        this.results = results;
    }

    /**
     * Clase interna que representa el resultado de un jugador en la partida.
     */
    public static class PlayerResult {
        private String username;
        private int finalScore;
        private int position;
        private boolean won;

        public PlayerResult(String username, int finalScore, int position, boolean won) {
            this.username = username;
            this.finalScore = finalScore;
            this.position = position;
            this.won = won;
        }

        // Getters
        public String getUsername() {
            return username;
        }

        public int getFinalScore() {
            return finalScore;
        }

        public int getPosition() {
            return position;
        }

        public boolean isWon() {
            return won;
        }

        // Setters
        public void setUsername(String username) {
            this.username = username;
        }

        public void setFinalScore(int finalScore) {
            this.finalScore = finalScore;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setWon(boolean won) {
            this.won = won;
        }
    }
} 