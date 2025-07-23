package com.serpentia.service;

import com.serpentia.enums.GameMode;
import com.serpentia.model.BoardState;
import com.serpentia.model.Point;
import com.serpentia.model.Player;
import com.serpentia.repository.GameRepository;
import com.serpentia.websocket.GameEvent;
import com.serpentia.websocket.ScoreEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SimpMessagingTemplate ws;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameRepository, ws, eventPublisher);
    }

    @Test
    void testInitRoom_createsBoardAndSendsStartEvent() {
        String roomId = "room1";
        List<String> players = Arrays.asList("player1", "player2");

        gameService.initRoom(roomId, players, GameMode.COMPETITIVE, 100);

        verify(gameRepository).saveBoard(any(BoardState.class));
        verify(ws, times(1)).convertAndSend(contains(roomId), (Object) argThat(event -> {
            return event instanceof GameEvent && ((GameEvent) event).getType().equals("START");
        }));
    }

    @Test
    void testSetDirection_whenInGame_updatesDirectionAndSaves() {
        String roomId = "room1";
        String player = "player1";
        String direction = "UP";

        BoardState board = mock(BoardState.class);
        Map<String, String> directions = new HashMap<>();
        Player p = new Player(player, "#FF0000", new Point(0, 0));

        when(gameRepository.getBoard(roomId)).thenReturn(board);
        when(board.getStatus()).thenReturn("IN_GAME");
        when(board.getSnakeDirections()).thenReturn(directions);
        when(board.getPlayers()).thenReturn(Map.of(player, p));

        doNothing().when(board).reconstructPlayersIfNeeded();

        gameService.setDirection(roomId, player, direction);

        assertEquals(direction, directions.get(player));
        verify(gameRepository).saveBoard(board);
    }


    @Test
    void testSetDirection_boardNull_doesNothing() {
        when(gameRepository.getBoard("room1")).thenReturn(null);
        gameService.setDirection("room1", "player1", "LEFT");
        verify(gameRepository, never()).saveBoard(any());
    }

    @Test
    void testGetBoardState_returnsExpectedBoard() {
        String roomId = "room1";
        BoardState board = new BoardState();
        when(gameRepository.getBoard(roomId)).thenReturn(board);

        BoardState result = gameService.getBoardState(roomId);

        assertEquals(board, result);
    }

    @Test
    void testDeleteAllGames_delegatesToRepository() {
        gameService.deleteAllGames();
        verify(gameRepository).deleteAllGames();
    }
    @Test
    void testGameLoop_playerEatsFruit_boardUpdatedAndEventsSent() {
        String roomId = "room1";
        String redisKey = "game:" + roomId;


        BoardState board = new BoardState();
        board.setRoomId(roomId);
        board.setStatus("IN_GAME");
        board.setWidth(10);
        board.setHeight(10);
        board.setGameMode(GameMode.COMPETITIVE);
        board.setTargetScore(100);

        String player = "player1";
        Point start = new Point(5, 5);
        Point fruit = new Point(5, 4); // justo arriba

        board.addPlayer(player, "#FF0000", start);
        board.getSnakeDirections().put(player, "UP");
        board.getFruits().add(fruit);


        when(gameRepository.getAllGameKeys()).thenReturn(Set.of(redisKey));
        when(gameRepository.getBoard(roomId)).thenReturn(board);


        gameService.gameLoop();


        verify(gameRepository).saveBoard(board);


        verify(ws, atLeastOnce()).convertAndSend(contains(roomId), any(GameEvent.class));
        verify(ws).convertAndSend((String) eq("/topic/game/" + roomId), (Object) argThat(event ->
                event instanceof GameEvent && ((GameEvent) event).getType().equals("FRUIT")
        ));
        verify(ws).convertAndSend((String) eq("/topic/game/" + roomId), (Object) argThat(event ->
                event instanceof ScoreEvent && ((ScoreEvent) event).getType().equals("SCORE_UPDATE")
        ));


        assertTrue(board.getPlayerScore(player) >= 10);
    }

}
