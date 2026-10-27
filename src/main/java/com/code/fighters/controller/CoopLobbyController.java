package com.code.fighters.controller;

import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.enums.UltimateSkill;
import com.code.fighters.model.GameRoom;
import com.code.fighters.repository.GameRoomRepository;
import com.code.fighters.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coop")
@RequiredArgsConstructor
public class CoopLobbyController {

    private final GameRoomRepository gameRoomRepository;
    private final PlayerRepository playerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> createRoom(@RequestBody CreateRoomRequest request) {
        int maxPlayers = Math.min(Math.max(request.maxPlayers(), 2), 4);
        GameRoom room = new GameRoom(maxPlayers);
        room.addPlayer(request.playerId());
        room.setUltimate(request.playerId(), skillOf(request.playerId()));
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
                        return ResponseEntity.status(400).<CoopGameState>build();
                    }
                    room.setUltimate(request.playerId(), skillOf(request.playerId()));

                    messagingTemplate.convertAndSend(
                            "/topic/game/" + roomId,
                            room.toGameState());

                    return ResponseEntity.ok(room.toGameState());
                })
                .orElseGet(() -> ResponseEntity.notFound().<CoopGameState>build());
    }

    @PostMapping("/leave/{roomId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable String roomId,
            @RequestBody JoinRoomRequest request) {
        return gameRoomRepository.findById(roomId)
                .map(room -> {
                    room.removePlayer(request.playerId());
                    messagingTemplate.convertAndSend("/topic/game/" + roomId, room.toGameState());
                    if (room.getPlayers().isEmpty()) {
                        gameRoomRepository.remove(roomId);
                    }
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
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

    private UltimateSkill skillOf(String playerId) {
        try {
            return playerRepository.findById(Long.parseLong(playerId))
                    .map(Player::getUltimateSkill)
                    .orElse(UltimateSkill.FRIDAY_DEPLOY);
        } catch (NumberFormatException e) {
            return UltimateSkill.FRIDAY_DEPLOY;
        }
    }

    public record CreateRoomRequest(String playerId, int maxPlayers) {
    }

    public record JoinRoomRequest(String playerId) {
    }
}