package com.code.fighters.dto.response;

public record UltimateConfigResponseDTO(
    String name,
    int healAmount,
    boolean grantsInvincibility,
    int invincibilityMs,
    int tickDamage,
    int tickCount,
    int tickIntervalMs,
    int cloneDamage,
    int cooldownMs
) {

}
