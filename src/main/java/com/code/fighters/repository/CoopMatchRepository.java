package com.code.fighters.repository;

import com.code.fighters.entity.CoopMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoopMatchRepository extends JpaRepository<CoopMatch, Long> {

    Optional<CoopMatch> findByRoomId(String roomId);
}