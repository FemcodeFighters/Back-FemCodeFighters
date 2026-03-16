package com.code.fighters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.fighters.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{

    Optional<Player> findByUserId(Long userId);
}
