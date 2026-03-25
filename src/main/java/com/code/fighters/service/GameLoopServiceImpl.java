package com.code.fighters.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.code.fighters.dto.coop.CoopGameState;
import com.code.fighters.model.GamePhase;
import com.code.fighters.model.GameRoom;
import com.code.fighters.repository.GameRoomRepository;
import com.code.fighters.service.MatchServiceImpl.PlayerMatchStats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameLoopServiceImpl implements GameLoopService {

    private final GameRoomRepository gameRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchService matchService;

    @Override
    @Scheduled(fixedRate = 50)
    public void gameTick() {
        for (GameRoom room : gameRoomRepository.findAll()) {

            if (room.getPhase() == GamePhase.PLAYING) {
                room.processInputs();
                room.updateEnemy();
                room.checkCollisions();
                broadcastState(room);
                room.resetAllAttacks();
            }

            if (room.getPhase() == GamePhase.WIN || room.getPhase() == GamePhase.LOSE) {
                broadcastState(room);
                persistMatch(room);
                log.info("Sala {} finalizada. Victoria: {}. Eliminando sala...",
                        room.getRoomId(), room.getPhase() == GamePhase.WIN);
                gameRoomRepository.remove(room.getRoomId());
            }
        }
    }

    private void broadcastState(GameRoom room) {
        try {
            CoopGameState state = room.toGameState();
            messagingTemplate.convertAndSend("/topic/game/" + room.getRoomId(), state);
        } catch (Exception e) {
            log.error("Error enviando broadcast a sala {}: {}", room.getRoomId(), e.getMessage());
        }
    }

    private void persistMatch(GameRoom room) {
        boolean victory = room.getPhase() == GamePhase.WIN;
        Map<Long, PlayerMatchStats> statsMap = new HashMap<>();

        room.getPlayers().forEach((playerIdStr, state) -> {
            try {
                String cleanId = playerIdStr.split(" ")[0].replaceAll("[^0-9]", "");
                Long playerId = Long.parseLong(cleanId);

                statsMap.put(playerId, new PlayerMatchStats(
                        state.damageDealt(),
                        state.damageTaken(),
                        state.ultimatesUsed(),
                        state.alive()));
            } catch (NumberFormatException e) {
                log.warn("No se pudo parsear el playerId '{}' a Long en la sala {}", playerIdStr, room.getRoomId());
            }
        });

        try {
            matchService.saveMatch(
                    room.getRoomId(),
                    room.getStartedAt(),
                    victory,
                    statsMap);
        } catch (Exception e) {
            log.error("Error persistiendo partida {}: {}", room.getRoomId(), e.getMessage());
        }
    }
}