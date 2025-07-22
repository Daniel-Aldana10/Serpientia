package com.serpentia;

import com.serpentia.model.Player;
import com.serpentia.model.Team;
import com.serpentia.enums.GameMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class BoardState implements Serializable {
    private String roomId;
    private int width = 40;
    private int height = 30;
    private Map<String, Deque<Point>> snakePositions = new HashMap<>();
    private Map<String, String> snakeDirections = new HashMap<>();
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Team> teams = new HashMap<>();
    private Map<String, String> playerToTeam = new HashMap<>();
    private List<Point> fruits = new ArrayList<>();
    private String status; // WAITING, IN_GAME, FINISHED
    private GameMode gameMode = GameMode.COMPETITIVE; // Modo de juego por defecto
    private int targetScore; // Puntuación objetivo para ganar
    @JsonIgnore
    private static final String team1s = "team1";
    @JsonIgnore
    private static final String team2s = "team2";
    @JsonIgnore
    private static final Random r = new Random();




    @JsonIgnore
    public void spawnFruit() {
        while (true) {
            Point candidate = new Point(r.nextInt(width), r.nextInt(height));
            boolean collision = snakePositions.values().stream()
                    .flatMap(d -> d.stream())
                    .anyMatch(pt -> pt.equals(candidate));

            if (!collision) {
                fruits.add(candidate);
                break;
            }
        }
    }

    /**
     * Agrega un jugador al juego
     * @param playerName Nombre del jugador
     * @param color Color de la serpiente
     * @param initialPosition Posición inicial
     */
    @JsonIgnore
    public void addPlayer(String playerName, String color, Point initialPosition) {
        Player player = new Player(playerName, color, initialPosition);
        players.put(playerName, player);
        snakePositions.put(playerName, player.getSnake());
        snakeDirections.put(playerName, player.getDirection());
    }

    /**
     * Obtiene un jugador por su ID
     * @param playerId ID del jugador
     * @return Jugador o null si no existe
     */
    @JsonIgnore
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    /**
     * Obtiene todos los jugadores vivos
     * @return Lista de jugadores vivos
     */
    @JsonIgnore
    public List<Player> getAlivePlayers() {
        return players.values().stream()
                .filter(Player::isAlive)
                .toList();
    }

    /**
     * Obtiene el jugador con mayor puntaje
     * @return Jugador con mayor puntaje o null si no hay jugadores
     */
    @JsonIgnore
    public Player getPlayerWithHighestScore() {
        return players.values().stream()
                .filter(Player::isAlive)
                .max(Comparator.comparingInt(Player::getScore))
                .orElse(null);
    }

    /**
     * Actualiza el puntaje de un jugador cuando come fruta
     * @param playerId ID del jugador
     * @param points Puntos a agregar
     */
    @JsonIgnore
    public void addScoreToPlayer(String playerId, int points) {
        Player player = players.get(playerId);
        if (player != null) {
            player.addScore(points);
        }
    }

    /**
     * Elimina un jugador del juego
     * @param playerId ID del jugador a eliminar
     */
    @JsonIgnore
    public void eliminatePlayer(String playerId) {
        Player player = players.get(playerId);
        if (player != null) {
            player.eliminate();
        }
        snakePositions.remove(playerId);
        snakeDirections.remove(playerId);
    }

    /**
     * Obtiene el número de jugadores vivos
     * @return Número de jugadores vivos
     */
    @JsonIgnore
    public int getAlivePlayerCount() {
        return (int) players.values().stream()
                .filter(Player::isAlive)
                .count();
    }

    /**
     * Obtiene el puntaje de un jugador específico
     * @param playerId ID del jugador
     * @return Puntaje del jugador o 0 si no existe
     */
    @JsonIgnore
    public int getPlayerScore(String playerId) {
        Player player = players.get(playerId);
        return player != null ? player.getScore() : 0;
    }

    // Elimino @JsonIgnore para que players se serialice correctamente
    public Map<String, Player> getPlayers() {
        return players;
    }

    /**
     * Reconstruye el mapa de jugadores desde snakePositions si está vacío.
     * Esto es necesario cuando el BoardState se deserializa desde Redis y
     * el mapa players se pierde pero snakePositions se mantiene.
     */
    @JsonIgnore
    public void reconstructPlayersIfNeeded() {
        if (players.isEmpty() && !snakePositions.isEmpty()) {
            String[] colors = {"red", "green", "blue", "yellow", "magenta", "cyan"};
            int colorIndex = 0;
            
            for (String playerId : snakePositions.keySet()) {
                Deque<Point> snake = snakePositions.get(playerId);
                String direction = snakeDirections.get(playerId);
                
                if (!snake.isEmpty()) {
                    Point initialPosition = snake.peekFirst();
                    String color = colors[colorIndex % colors.length];
                    
                    Player player = new Player(playerId, color, initialPosition);
                    player.setSnake(snake);
                    player.setDirection(direction);
                    

                    if (!snakePositions.containsKey(playerId)) {
                        player.eliminate();
                    }
                    
                    players.put(playerId, player);
                    colorIndex++;

                }
            }

        }
    }

    /**
     * Asigna equipos automáticamente basándose en el orden de llegada de los jugadores.
     */
    @JsonIgnore
    public void assignTeamsAutomatically() {
        if (gameMode == GameMode.TEAM && players.size() == 4) {
            List<String> playerIds = new ArrayList<>(players.keySet());

            // Equipo 1: jugadores 0 y 1
            Team team1 = new Team(team1s, Arrays.asList(playerIds.get(0), playerIds.get(1)));
            // Equipo 2: jugadores 2 y 3
            Team team2 = new Team(team2s, Arrays.asList(playerIds.get(2), playerIds.get(3)));

            teams.put(team1s, team1);
            teams.put(team2s, team2);

            // Mapear jugadores a equipos
            for (int i = 0; i < 4; i++) {
                String teamId = i < 2 ? team1s : team2s;
                playerToTeam.put(playerIds.get(i), teamId);
            }
        }
    }
    
    /**
     * Verifica si el juego ha terminado basándose en el modo de juego.
     * Para modo competitivo: cuando queda1 jugador o menos.
     * Para modo equipo: cuando queda 1 equipo o menos con jugadores vivos.
     */
    @JsonIgnore
    public boolean isGameFinished() {
        // Condición de meta por puntuación (individual o por equipo)
        if (gameMode == GameMode.COMPETITIVE) {
            for (Player player : players.values()) {
                if (player.getScore() >= targetScore) {
                    return true;
                }
            }
            return getAlivePlayerCount() <= 1;
        } else if (gameMode == GameMode.TEAM) {
            for (Team team : teams.values()) {
                if (team.getTeamScore() >= targetScore) {
                    return true;
                }
            }
            return getAliveTeamCount() <= 1;
        }
        return false;
    }
    
    /**
     * Obtiene el número de equipos con jugadores vivos
     * @return Número de equipos vivos
     */
    @JsonIgnore
    public int getAliveTeamCount() {
        Set<String> aliveTeams = new HashSet<>();
        
        for (Player player : players.values()) {
            if (player.isAlive()) {
                String teamId = playerToTeam.get(player.getName());
                if (teamId != null) {
                    aliveTeams.add(teamId);
                }
            }
        }
        
        return aliveTeams.size();
    }
    
    /**
     * Obtiene el equipo ganador (el que tiene jugadores vivos)
     * @return ID del equipo ganador o null si no hay ganador
     */
    @JsonIgnore
    public String getWinningTeam() {
        for (Team team : teams.values()) {
            if (team.hasAlivePlayers(getAlivePlayerNames())) {
                return team.getTeamId();
            }
        }
        return null;
    }
    
    /**
     * Obtiene la lista de nombres de jugadores vivos
     * @return Lista de nombres de jugadores vivos
     */
    @JsonIgnore
    private List<String> getAlivePlayerNames() {
        return players.values().stream()
            .filter(Player::isAlive)
            .map(Player::getName)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el equipo de un jugador
     * @param playerId ID del jugador
     * @return ID del equipo o null si no está en ningún equipo
     */
    @JsonIgnore
    public String getPlayerTeam(String playerId) {
        return playerToTeam.get(playerId);
    }
    
    /**
     * Obtiene todos los equipos
     * @return Mapa de equipos
     */
    @JsonIgnore
    public Map<String, Team> getTeams() {
        return teams;
    }
    
    /**
     * Obtiene el mapeo de jugadores a equipos
     * @return Mapa de jugador -> equipo
     */
    public Map<String, String> getPlayerToTeam() {
        return playerToTeam;
    }
}