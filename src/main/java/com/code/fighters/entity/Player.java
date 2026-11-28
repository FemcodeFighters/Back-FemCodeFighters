package com.code.fighters.entity;

import java.time.LocalDateTime;

import com.code.fighters.entity.enums.UltimateSkill;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "characters")
@Data
@AllArgsConstructor
@Builder
public class Player {

    public static final int MAX_HEALTH = 100;
    public static final String DEFAULT_SKIN_COLOR = "#f5c5a3";
    public static final String DEFAULT_HAIR_STYLE = "ponytail";
    public static final String DEFAULT_HAIR_COLOR = "#7c3aed";
    public static final String DEFAULT_EYE_COLOR = "#2563eb";
    public static final String DEFAULT_OUTFIT = "hoodie";
    public static final String DEFAULT_OUTFIT_COLOR = "#1e1b4b";
    public static final String DEFAULT_ACCESSORY = "none";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private int health = MAX_HEALTH;

    @Builder.Default
    private int wins = 0;

    @Builder.Default
    private int losses = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "skin_color")
    @Builder.Default
    private String skinColor = DEFAULT_SKIN_COLOR;

    @Column(name = "hair_style")
    @Builder.Default
    private String hairStyle = DEFAULT_HAIR_STYLE;

    @Column(name = "hair_color")
    @Builder.Default
    private String hairColor = DEFAULT_HAIR_COLOR;

    @Column(name = "eye_color")
    @Builder.Default
    private String eyeColor = DEFAULT_EYE_COLOR;

    @Column(name = "outfit")
    @Builder.Default
    private String outfit = DEFAULT_OUTFIT;

    @Column(name = "outfit_color")
    @Builder.Default
    private String outfitColor = DEFAULT_OUTFIT_COLOR;

    @Column(name = "accessory")
    @Builder.Default
    private String accessory = DEFAULT_ACCESSORY;

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "ultimate_skill")
    @Builder.Default
    private UltimateSkill ultimateSkill = UltimateSkill.FRIDAY_DEPLOY;

    private LocalDateTime lastUltimateUsed;

    protected Player() {}

    /** Vuelve la apariencia a los valores por defecto (no toca la ultimate ni las estadísticas). */
    public void resetAppearance() {
        this.skinColor = DEFAULT_SKIN_COLOR;
        this.hairStyle = DEFAULT_HAIR_STYLE;
        this.hairColor = DEFAULT_HAIR_COLOR;
        this.eyeColor = DEFAULT_EYE_COLOR;
        this.outfit = DEFAULT_OUTFIT;
        this.outfitColor = DEFAULT_OUTFIT_COLOR;
        this.accessory = DEFAULT_ACCESSORY;
    }
}