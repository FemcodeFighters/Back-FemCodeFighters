package com.code.fighters.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.code.fighters.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{

    Optional<Player> findByUserId(Long userId);
    @Query("SELECT p FROM Player p WHERE p.id <> :id")
    List<Player> findAllExcept(Long id);

    List<Player> findTop10ByOrderByWinsDesc();
}
