package com.code.fighters.controller;

import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.model.GameRoom;
import com.code.fighters.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coop")
@RequiredArgsConstructor
public class CoopLobbyController {

    private final GameRoomRepository gameRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> createRoom(@RequestBody CreateRoomRequest request) {
        GameRoom room = new GameRoom(request.maxPlayers());
        room.addPlayer(request.playerId());
        gameRoomRepository.save(room);
        return ResponseEntity.ok(room.getRoomId());
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<CoopGameState> joinRoom(@PathVariable String roomId,
            @RequestBody JoinRoomRequest request) {
        return gameRoomRepository.findById(roomId)
                .map(room -> {
                    boolean joined = room.addPlayer(request.playerId());
                    if (!joined) {
                        return ResponseEntity.status(400).<CoopGameState>body(null);
                    }

                    messagingTemplate.convertAndSend(
                            "/topic/game/" + roomId,
                            room.toGameState());

                    return ResponseEntity.ok(room.toGameState());
                })
                .orElseGet(() -> ResponseEntity.status(404).<CoopGameState>body(null));
    }

    @PostMapping("/start/{roomId}")
    public ResponseEntity<Void> startGame(@PathVariable String roomId) {
        return gameRoomRepository.findById(roomId)
                .map(room -> {
                    room.startGame();

                    messagingTemplate.convertAndSend(
                            "/topic/game/" + roomId,
                            room.toGameState());

                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<CoopGameState> getRoom(@PathVariable String roomId) {
        return gameRoomRepository.findById(roomId)
                .map(room -> ResponseEntity.ok(room.toGameState()))
                .orElseGet(() -> ResponseEntity.notFound().<CoopGameState>build());
    }

    public record CreateRoomRequest(String playerId, int maxPlayers) {
    }

    public record JoinRoomRequest(String playerId) {
    }
}
