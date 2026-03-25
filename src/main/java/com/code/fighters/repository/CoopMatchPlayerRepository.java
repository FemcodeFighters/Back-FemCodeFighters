package com.code.fighters.repository;

import com.code.fighters.entity.CoopMatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoopMatchPlayerRepository extends JpaRepository<CoopMatchPlayer, Long> {

    List<CoopMatchPlayer> findByMatchId(Long matchId);

    List<CoopMatchPlayer> findByPlayerId(Long playerId);
}