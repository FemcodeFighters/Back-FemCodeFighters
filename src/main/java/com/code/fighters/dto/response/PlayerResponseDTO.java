package com.code.fighters.dto.response;

import java.time.LocalDateTime;

public record PlayerResponseDTO(
    String skinColor,
    String hairStyle,
    String hairColor,
    String eyeColor,
    String outfit,
    String outfitColor,
    String accessory,
    LocalDateTime updatedAt
) {

}
