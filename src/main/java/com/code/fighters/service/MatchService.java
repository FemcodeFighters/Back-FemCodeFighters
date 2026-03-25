package com.code.fighters.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.code.fighters.entity.CoopMatch;
import com.code.fighters.service.MatchServiceImpl.PlayerMatchStats;

public interface MatchService {
    public void saveMatch(String roomId, LocalDateTime startedAt, boolean victory,
            Map<Long, PlayerMatchStats> playerStats);

    public long getDurationSeconds(CoopMatch match);

    public int getPlayerCount(CoopMatch match);
}
