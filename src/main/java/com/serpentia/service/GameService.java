package com.serpentia.service;


import com.serpentia.BoardState;

import com.serpentia.Point;
import com.serpentia.model.Player;
import com.serpentia.repository.GameRepository;
import com.serpentia.websocket.GameEvent;
import com.serpentia.websocket.ScoreEvent;
import com.serpentia.dto.PlayerDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.serpentia.websocket.PlayerEliminatedEvent;
import com.serpentia.websocket.GameFinishedEvent;
import java.util.stream.Collectors;

import java.util.*;
import com.serpentia.enums.GameMode;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate ws;
    private final ApplicationEventPublisher eventPublisher;
    private final String topic = "/topic/game/";
    public GameService(GameRepository gameRepository, SimpMessagingTemplate ws, ApplicationEventPublisher eventPublisher) {
        this.gameRepository = gameRepository;
        this.ws = ws;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Asigna colores a los jugadores según el índice
     * @param playerIndex Índice del jugador (0-based)
     * @return Color asignado al jugador en formato hexadecimal
     */
    private String assignPlayerColor(int playerIndex) {

        String[] teamColors = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00"};
        return teamColors[playerIndex % teamColors.length];

    }

    public void initRoom(String roomId, List<String> players, GameMode gameMode) {
        BoardState board = new BoardState();
        board.setRoomId(roomId);
        board.setStatus("IN_GAME");
        board.setGameMode(gameMode);

        for (int i = 0; i < players.size(); i++) {
            String playerId = players.get(i);
            String color = assignPlayerColor(i);
            Point initialPosition = new Point(0, i * 2);
            board.addPlayer(playerId, color, initialPosition);
        }
        // Asignar equipos automáticamente si es modo TEAM
        board.assignTeamsAutomatically();

        for (int i = 0; i < 5; i++) board.spawnFruit();
        gameRepository.saveBoard(board);
        System.out.println(roomId);
        ws.convertAndSend(topic + roomId, new com.serpentia.websocket.GameEvent("START", null, board));
    }

    public void setDirection(String roomId, String player, String dir) {
       BoardState board = gameRepository.getBoard(roomId);
        if (board != null && "IN_GAME".equals(board.getStatus())) {

            board.reconstructPlayersIfNeeded();
            
            board.getSnakeDirections().put(player, dir);
            gameRepository.saveBoard(board);
        }
    }

    @Scheduled(fixedRate = 200)
    public void gameLoop() {
        Set<String> keys = gameRepository.getAllGameKeys();

        if (keys == null) return;

        for (String key : keys) {
            String roomId = key.replace("game:", "");
            BoardState board = gameRepository.getBoard(roomId);

            if (board == null || !"IN_GAME".equals(board.getStatus())) continue;


            if (board.getPlayers().isEmpty() && !board.getSnakePositions().isEmpty()) {
                board.reconstructPlayersIfNeeded();
            }

            updateBoard(board);
            gameRepository.saveBoard(board);
            System.out.println(board);
            ws.convertAndSend(topic + roomId,  new GameEvent("UPDATE", null, board));
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
               

                PlayerEliminatedEvent event = new PlayerEliminatedEvent(
                    p, b.getRoomId(), b
                        .getPlayerScore(p), b.getAlivePlayerCount() +1               );
                eventPublisher.publishEvent(event);
                
                ws.convertAndSend(topic + b.getRoomId(), new GameEvent("COLLISION", p, b));
                continue;
            }
            for (Deque<Point> other : snakes.values()) {
                if (other.contains(nh)) {
                    eliminated.add(p);
                   

                    PlayerEliminatedEvent event = new PlayerEliminatedEvent(
                        p, b.getRoomId(), b.getPlayerScore(p), b.getAlivePlayerCount() + 1
                    );
                    eventPublisher.publishEvent(event);
                   
                    ws.convertAndSend(topic + b.getRoomId(), new GameEvent("COLLISION", p, b));
                    break;
                }
            }
        }

        for (String p : newHeads.keySet()) {
            if (eliminated.contains(p)) {
                b.eliminatePlayer(p);
                continue;
            }

            Deque<Point> body = snakes.get(p);
            Point nh = newHeads.get(p);
            body.addFirst(nh);

            if (b.getFruits().remove(nh)) {
                b.spawnFruit();
                ateFruit.add(p);
                

                b.addScoreToPlayer(p, 10);

                if (b.getGameMode() == GameMode.TEAM) {
                    String teamId = b.getPlayerTeam(p);
                    if (teamId != null && b.getTeams().containsKey(teamId)) {
                        b.getTeams().get(teamId).addScore(10);
                    }
                }


                ws.convertAndSend(topic + b.getRoomId(), new GameEvent("FRUIT", p, b));
                

                List<PlayerDTO> playerDTOs = b.getPlayers().values().stream()
                        .map(PlayerDTO::new)
                        .toList();
                ws.convertAndSend(topic + b.getRoomId(),
                    new ScoreEvent("SCORE_UPDATE", playerDTOs));
            } else {
                body.pollLast();
            }
        }


        ws.convertAndSend(topic + b.getRoomId(), new GameEvent("UPDATE", null, b));

        if (b.isGameFinished()) {
            b.setStatus("FINISHED");
            

            List<GameFinishedEvent.PlayerResult> results = b.getPlayers().values().stream()
                .map(player -> new GameFinishedEvent.PlayerResult(
                    player.getName(),
                    player.getScore(),
                    calculatePosition(player, b), // Método que calcule la posición
                    isPlayerWinner(player, b) // Determinar si el jugador ganó
                ))
                .collect(Collectors.toList());
            
            GameFinishedEvent gameFinishedEvent = new GameFinishedEvent(b.getRoomId(), results);
            eventPublisher.publishEvent(gameFinishedEvent);
            
            ws.convertAndSend(topic + b.getRoomId(), new GameEvent("END", null, b));
            gameRepository.deleteBoard(b.getRoomId());
        }
    }
    public void deleteAllGames() {
        gameRepository.deleteAllGames();
    }
    
    /**
     * Obtiene el estado del tablero para una sala específica
     * @param roomId iD de la sala
     * @return Estado del tablero o null si no existe
     */
    public BoardState getBoardState(String roomId) {
        return gameRepository.getBoard(roomId);
    }

    /**
     * Calcula la posición final de un jugador basándose en su puntuación.
     * Los jugadores con mayor puntuación tienen mejor posición.
     * 
     * @param player Jugador para calcular posición
     * @param board Estado del tablero
     * @return Posición del jugador (1 = primero, 2 = segundo, etc.)
     */
    private int calculatePosition(Player player, BoardState board) {
        int playerScore = player.getScore();
        int position = 1;
        
        for (Player otherPlayer : board.getPlayers().values()) {
            if (otherPlayer.getScore() > playerScore) {
                position++;
            }
        }
        
        return position;
    }

    /**
     * Determina si un jugador ganó basándose en el modo de juego
     * @param player Jugador a evaluar
     * @param board Estado del tablero
     * @return true si el jugador ganó
     */
    private boolean isPlayerWinner(Player player, BoardState board) {
        if (board.getGameMode() == GameMode.COMPETITIVE) {
            return player.isAlive(); // El último vivo gana
        } else if (board.getGameMode() == GameMode.TEAM) {
            String playerTeam = board.getPlayerTeam(player.getName());
            String winningTeam = board.getWinningTeam();
            return playerTeam != null && playerTeam.equals(winningTeam);
        }
        return false;
    }
}