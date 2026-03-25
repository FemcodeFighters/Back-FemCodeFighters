package com.code.fighters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coop_match_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoopMatchPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private CoopMatch match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "damage_dealt", nullable = false)
    @Builder.Default
    private int damageDealt = 0;

    @Column(name = "damage_taken", nullable = false)
    @Builder.Default
    private int damageTaken = 0;

    @Column(name = "ultimates_used", nullable = false)
    @Builder.Default
    private int ultimatesUsed = 0;

    @Column(nullable = false)
    private boolean survived;
}
