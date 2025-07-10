package com.serpentia;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
public class BoardState implements Serializable {
    private String roomId;
    private int width = 40;
    private int height = 30;
    private Map<String, Deque<Point>> snakePositions = new HashMap<>();
    private Map<String, String> snakeDirections = new HashMap<>(); // "UP","DOWN",...
    private List<Point> fruits = new ArrayList<>();
    //private List<PowerUp> powerUps = new ArrayList<>();
    private String status; // WAITING, IN_GAME, FINISHED

    public void spawnFruit() {
        Random r = new Random();
        while (true) {
            // candidate es una variable local nueva en cada iteración
            Point candidate = new Point(r.nextInt(width), r.nextInt(height));

            // ahora candidate es efectivamente final dentro del lambda
            boolean collision = snakePositions.values().stream()
                    .flatMap(d -> d.stream())
                    .anyMatch(pt -> pt.equals(candidate));

            if (!collision) {
                fruits.add(candidate);
                break;
            }
            // si hay colisión, volvemos a iterar
        }
    }



}