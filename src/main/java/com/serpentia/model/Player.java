package com.serpentia.model;

import com.serpentia.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 * Clase que representa un jugador durante una partida de Serpentia.
 * Maneja el estado del jugador incluyendo puntajes, posición de la serpiente,
 * dirección y estado de vida.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Player implements Serializable {
    
    /**
     * Nombre del jugador
     */
    private String name;
    
    /**
     * Color de la serpiente del jugador
     */
    private String color;
    
    /**
     * Posiciones de la serpiente (cuerpo completo)
     */
    private Deque<Point> snake;
    
    /**
     * Dirección actual del movimiento
     */
    private String direction;
    
    /**
     * Puntaje actual en la partida
     */
    private int score;
    
    /**
     * Indica si el jugador está vivo
     */
    private boolean alive;
    
    /**
     * Puntaje máximo alcanzado en esta partida
     */
    private int maxScore;

    
    /**
     * Constructor principal para crear un jugador
     */
    public Player(String name, String color, Point initialPosition) {
        this.name = name;
        this.color = color;
        this.snake = new ArrayDeque<>();
        this.snake.add(initialPosition);
        this.direction = "RIGHT";
        this.score = 0;
        this.alive = true;
        this.maxScore = 0;
    }
    
    /**
     * Incrementa el puntaje del jugador
     * @param points Puntos a agregar
     */
    @JsonIgnore
    public void addScore(int points) {
        this.score += points;
        if (this.score > this.maxScore) {
            this.maxScore = this.score;
        }
    }
    
    /**
     * Marca al jugador como eliminado
     */
    @JsonIgnore
    public void eliminate() {
        this.alive = false;
    }

    /**
     * Obtiene la dirección actual del jugador
     */
    public String getDirection() {
        return this.direction;
    }

    /**
     * Establece la dirección actual del jugador
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }
} 