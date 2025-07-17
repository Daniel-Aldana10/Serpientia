package com.serpentia.model;

import com.serpentia.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    
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
     * Obtiene la cabeza de la serpiente
     * @return Punto de la cabeza
     */
    @JsonIgnore
    public Point getHead() {
        return this.snake.peekFirst();
    }
    
    /**
     * Obtiene la cola de la serpiente
     * @return Punto de la cola
     */
    @JsonIgnore
    public Point getTail() {
        return this.snake.peekLast();
    }
    
    /**
     * Verifica si una posición está ocupada por la serpiente
     * @param position Posición a verificar
     * @return true si la posición está ocupada
     */
    @JsonIgnore
    public boolean occupiesPosition(Point position) {
        return this.snake.contains(position);
    }
    
    /**
     * Agrega una nueva posición a la cabeza de la serpiente
     * @param newHead Nueva posición de la cabeza
     */
    @JsonIgnore
    public void addHead(Point newHead) {
        this.snake.addFirst(newHead);
    }
    
    /**
     * Remueve la cola de la serpiente (cuando no come fruta)
     */
    @JsonIgnore
    public void removeTail() {
        this.snake.pollLast();
    }
    
    /**
     * Obtiene el tamaño actual de la serpiente
     * @return Número de segmentos de la serpiente
     */
    @JsonIgnore
    public int getSnakeLength() {
        return this.snake.size();
    }
} 