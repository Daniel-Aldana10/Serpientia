package com.serpentia.service;


import com.serpentia.BoardState;
import com.serpentia.BoardUpdate;
import com.serpentia.Point;
import com.serpentia.repository.GameRepository;
import com.serpentia.websocket.GameEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate ws;

    public GameService(GameRepository gameRepository, SimpMessagingTemplate ws) {
        this.gameRepository = gameRepository;
        this.ws = ws;
    }

    public void initRoom(String roomId, List<String> players) {
        BoardState board = new BoardState();
        board.setRoomId(roomId);
        board.setStatus("IN_GAME");

        for (String p : players) {
            Deque<Point> body = new ArrayDeque<>();
            body.add(new Point(0, players.indexOf(p) * 2)); // posición inicial
            board.getSnakePositions().put(p, body);
            board.getSnakeDirections().put(p, "RIGHT");
        }

        for (int i = 0; i < 5; i++) board.spawnFruit();
        gameRepository.saveBoard(board);
        System.out.println(roomId);
        ws.convertAndSend("/topic/game/" + roomId, new com.serpentia.websocket.GameEvent("START", null, board, "¡El juego ha comenzado!"));
    }

    public void setDirection(String roomId, String player, String dir) {
        BoardState board = gameRepository.getBoard(roomId);
        if (board != null && "IN_GAME".equals(board.getStatus())) {
            board.getSnakeDirections().put(player, dir);
            gameRepository.saveBoard(board);
        }
    }

    @Scheduled(fixedRate = 100)
    public void gameLoop() {
        Set<String> keys = gameRepository.getAllGameKeys();
        if (keys == null) return;

        for (String key : keys) {
            String roomId = key.replace("game:", "");
            BoardState board = gameRepository.getBoard(roomId);

            if (board == null || !"IN_GAME".equals(board.getStatus())) continue;

            updateBoard(board);
            gameRepository.saveBoard(board);
            System.out.println(board);
            ws.convertAndSend("/topic/game/" + roomId, new BoardUpdate(board));
        }
    }

    private void updateBoard(BoardState b) {
        Map<String, Deque<Point>> snakes = b.getSnakePositions();
        Map<String, String> dirs = b.getSnakeDirections();
        Set<String> eliminated = new HashSet<>();
        Set<String> ateFruit = new HashSet<>();

        Map<String, Point> newHeads = new HashMap<>();
        for (String p : snakes.keySet()) {
            Deque<Point> body = snakes.get(p);
            Point head = body.peekFirst();
            String dir = dirs.get(p);
            Point nh = switch (dir) {
                case "UP"    -> new Point(head.getX(), head.getY() - 1);
                case "DOWN"  -> new Point(head.getX(), head.getY() + 1);
                case "LEFT"  -> new Point(head.getX() - 1, head.getY());
                case "RIGHT" -> new Point(head.getX() + 1, head.getY());
                default      -> head;
            };
            newHeads.put(p, nh);
        }

        for (Map.Entry<String, Point> e : newHeads.entrySet()) {
            String p = e.getKey();
            Point nh = e.getValue();
            if (nh.getX() < 0 || nh.getX() >= b.getWidth()
                    || nh.getY() < 0 || nh.getY() >= b.getHeight()) {
                eliminated.add(p);
                ws.convertAndSend("/topic/game/" + b.getRoomId(), new GameEvent("COLLISION", p, b, p + " chocó con el borde!"));
                continue;
            }
            for (Deque<Point> other : snakes.values()) {
                if (other.contains(nh)) {
                    eliminated.add(p);
                    ws.convertAndSend("/topic/game/" + b.getRoomId(), new GameEvent("COLLISION", p, b, p + " chocó con otra serpiente!"));
                    break;
                }
            }
        }

        for (String p : newHeads.keySet()) {
            if (eliminated.contains(p)) {
                snakes.remove(p);
                dirs.remove(p);
                continue;
            }

            Deque<Point> body = snakes.get(p);
            Point nh = newHeads.get(p);
            body.addFirst(nh);

            if (b.getFruits().remove(nh)) {
                b.spawnFruit();
                ateFruit.add(p);
                ws.convertAndSend("/topic/game/" + b.getRoomId(), new GameEvent("FRUIT", p, b, p + " comió una fruta!"));
            } else {
                body.pollLast();
            }
        }

        // Evento de actualización general
        ws.convertAndSend("/topic/game/" + b.getRoomId(), new GameEvent("UPDATE", null, b, null));

        if (snakes.size() <= 1) {
            b.setStatus("FINISHED");
            ws.convertAndSend("/topic/game/" + b.getRoomId(), new GameEvent("END", null, b, "Juego terminado!"));
        }
    }
}