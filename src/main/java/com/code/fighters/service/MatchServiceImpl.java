package com.code.fighters.service;

import com.code.fighters.entity.CoopMatch;
import com.code.fighters.entity.CoopMatchPlayer;
import com.code.fighters.entity.Player;
import com.code.fighters.repository.CoopMatchRepository;
import com.code.fighters.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final CoopMatchRepository coopMatchRepository;
    private final PlayerRepository playerRepository;

    @Override
    @Transactional
    public void saveMatch(String roomId, LocalDateTime startedAt, boolean victory,
            Map<Long, PlayerMatchStats> playerStats) {

        CoopMatch match = CoopMatch.builder()
                .roomId(roomId)
                .startedAt(startedAt)
                .finishedAt(LocalDateTime.now())
                .victory(victory)
                .build();

        List<Player> playersToUpdate = new ArrayList<>();

        playerStats.forEach((playerId, stats) -> {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));

            CoopMatchPlayer participant = CoopMatchPlayer.builder()
                    .match(match)
                    .player(player)
                    .damageDealt(stats.getDamageDealt())
                    .damageTaken(stats.getDamageTaken())
                    .ultimatesUsed(stats.getUltimatesUsed())
                    .survived(stats.isSurvived())
                    .build();

            match.getParticipants().add(participant);

            if (victory)
                player.setWins(player.getWins() + 1);
            else
                player.setLosses(player.getLosses() + 1);

            playersToUpdate.add(player);
        });

        playerRepository.saveAll(playersToUpdate);
        coopMatchRepository.save(match);
    }

    @Override
    public long getDurationSeconds(CoopMatch match) {
        if (match.getFinishedAt() == null)
            return 0;
        return ChronoUnit.SECONDS.between(match.getStartedAt(), match.getFinishedAt());
    }

    @Override
    public int getPlayerCount(CoopMatch match) {
        return match.getParticipants().size();
    }

    @Data
    @AllArgsConstructor
    public static class PlayerMatchStats {
        private int damageDealt;
        private int damageTaken;
        private int ultimatesUsed;
        private boolean survived;
    }
}