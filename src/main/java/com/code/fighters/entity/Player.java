package com.code.fighters.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "skin_color")
    @Builder.Default
    private String skinColor = "#f5c5a3";

    @Column(name = "hair_style")
    @Builder.Default
    private String hairStyle = "ponytail";

    @Column(name = "hair_color")
    @Builder.Default
    private String hairColor = "#7c3aed";

    @Column(name = "eye_color")
    @Builder.Default
    private String eyeColor = "#2563eb";

    @Column(name = "outfit")
    @Builder.Default
    private String outfit = "hoodie";

    @Column(name = "outfit_color")
    @Builder.Default
    private String outfitColor = "#1e1b4b";

    @Column(name = "accessory")
    @Builder.Default
    private String accessory = "none";

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Player() {}
}

