package com.code.fighters.dto.coop;

import com.code.fighters.model.GamePhase;
import java.util.Map;

public record CoopGameState(
                String roomId,
                Map<String, PlayerState> players,
                EnemyState enemy,
                long tick,
                GamePhase phase,
                int maxPlayers) {
}
