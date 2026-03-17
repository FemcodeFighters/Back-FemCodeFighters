package com.code.fighters.dto.response;

import java.time.LocalDateTime;

public record PlayerResponseDTO(
    Long id,
    String username,
    int wins,
    int losses,
    String skinColor,
    String hairStyle,
    String hairColor,
    String eyeColor,
    String outfit,
    String outfitColor,
    String accessory,
    String ultimateSkill,
    LocalDateTime updatedAt
) {

}
