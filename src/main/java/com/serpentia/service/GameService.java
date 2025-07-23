package com.serpentia.service;


import com.serpentia.model.BoardState;
import com.serpentia.model.Point;
import com.serpentia.model.Player;
import com.serpentia.repository.GameRepository;
import com.serpentia.websocket.GameEvent;
import com.serpentia.websocket.ScoreEvent;
import com.serpentia.dto.PlayerDTO;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.serpentia.websocket.PlayerEliminatedEvent;
import com.serpentia.websocket.GameFinishedEvent;
import java.util.*;
import com.serpentia.enums.GameMode;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate ws;

    private static final String topic = "/topic/game/";
    private static final String game = "IN_GAME";

    public GameService(GameRepository gameRepository, SimpMessagingTemplate ws) {
        this.gameRepository = gameRepository;
        this.ws = ws;
    }

    private String assignPlayerColor(int playerIndex) {
        String[] teamColors = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00"};
        return teamColors[playerIndex % teamColors.length];
    }

    public void initRoom(String roomId, List<String> players, GameMode gameMode, int targetScore) {
        BoardState board = new BoardState();
        board.setRoomId(roomId);
        board.setStatus(game);
        board.setGameMode(gameMode);
        board.setTargetScore(targetScore);
        for (int i = 0; i < players.size(); i++) {
            String playerId = players.get(i);
            String color = assignPlayerColor(i);
            Point initialPosition = new Point(0, i * 2);
            board.addPlayer(playerId, color, initialPosition);
        }
        board.assignTeamsAutomatically();
        for (int i = 0; i < 5; i++) board.spawnFruit();
        gameRepository.saveBoard(board);
        GameEvent event = new GameEvent("START", null, board);
        ws.convertAndSend(topic + roomId, event);

    }

    public void setDirection(String roomId, String player, String dir) {
        BoardState board = gameRepository.getBoard(roomId);
        if (board != null && game.equals(board.getStatus())) {
            board.reconstructPlayersIfNeeded();
            board.getSnakeDirections().put(player, dir);
            Player p = board.getPlayers().get(player);
            if (p != null) {
                p.setDirection(dir);
            }
            gameRepository.saveBoard(board);
        }
    }

    @Scheduled(fixedRate = 200)
    @SchedulerLock(name = "gameLoopLock", lockAtLeastFor = "200ms", lockAtMostFor = "500ms")
    public void gameLoop() {
        Set<String> keys = gameRepository.getAllGameKeys();
        if (keys == null) return;

        for (String key : keys) {
            String roomId = key.replace("game:", "");
            BoardState board = gameRepository.getBoard(roomId);
            if (board == null || !game.equals(board.getStatus())) continue;

            if (board.getPlayers().isEmpty() && !board.getSnakePositions().isEmpty()) {
                board.reconstructPlayersIfNeeded();
            }
            updateBoard(board);
            gameRepository.saveBoard(board);
            GameEvent event = new GameEvent("UPDATE", null, board);
            ws.convertAndSend(topic + roomId, event);
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
                default       -> head;
            };
            newHeads.put(p, nh);
        }

        for (Map.Entry<String, Point> e : newHeads.entrySet()) {
            String p = e.getKey();
            Point nh = e.getValue();
            if (nh.getX() < 0 || nh.getX() >= b.getWidth() || nh.getY() < 0 || nh.getY() >= b.getHeight()) {
                eliminated.add(p);
                PlayerEliminatedEvent event = new PlayerEliminatedEvent(p, b.getRoomId(), b.getPlayerScore(p), b.getAlivePlayerCount() + 1);

                GameEvent gameEvent = new GameEvent("COLLISION", p, b);
                ws.convertAndSend(topic + b.getRoomId(), gameEvent);

                continue;
            }
            for (Deque<Point> other : snakes.values()) {
                if (other.contains(nh)) {
                    eliminated.add(p);
                    PlayerEliminatedEvent event = new PlayerEliminatedEvent(p, b.getRoomId(), b.getPlayerScore(p), b.getAlivePlayerCount() + 1);

                    GameEvent gameEvent = new GameEvent("COLLISION", p, b);
                    ws.convertAndSend(topic + b.getRoomId(), gameEvent);

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

                GameEvent fruitEvent = new GameEvent("FRUIT", p, b);
                ws.convertAndSend(topic + b.getRoomId(), fruitEvent);


                List<PlayerDTO> playerDTOs = b.getPlayers().values().stream().map(PlayerDTO::new).toList();
                ScoreEvent scoreEvent = new ScoreEvent("SCORE_UPDATE", playerDTOs, b.getRoomId());
                ws.convertAndSend(topic + b.getRoomId(), scoreEvent);

            } else {
                body.pollLast();
            }
        }

        GameEvent updateEvent = new GameEvent("UPDATE", null, b);
        ws.convertAndSend(topic + b.getRoomId(), updateEvent);



        if (b.isGameFinished()) {
            b.setStatus("FINISHED");
            List<GameFinishedEvent.PlayerResult> results = b.getPlayers().values().stream()
                    .map(player -> new GameFinishedEvent.PlayerResult(
                            player.getName(),
                            player.getScore(),
                            calculatePosition(player, b),
                            isPlayerWinner(player, b)))
                    .toList();
            GameFinishedEvent finishedEvent = new GameFinishedEvent(b.getRoomId(), results);


            GameEvent endEvent = new GameEvent("END", null, b);
            ws.convertAndSend(topic + b.getRoomId(), endEvent);
           ;

            gameRepository.deleteBoard(b.getRoomId());
        }
    }

    public void deleteAllGames() {
        gameRepository.deleteAllGames();
    }

    public BoardState getBoardState(String roomId) {
        return gameRepository.getBoard(roomId);
    }

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

    private boolean isPlayerWinner(Player player, BoardState board) {
        if (board.getGameMode() == GameMode.COMPETITIVE) {
            return player.isAlive();
        } else if (board.getGameMode() == GameMode.TEAM) {
            String playerTeam = board.getPlayerTeam(player.getName());
            String winningTeam = board.getWinningTeam();
            return playerTeam != null && playerTeam.equals(winningTeam);
        }
        return false;
    }
}
