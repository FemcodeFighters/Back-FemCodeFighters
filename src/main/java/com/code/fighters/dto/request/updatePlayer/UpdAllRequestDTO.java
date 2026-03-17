package com.code.fighters.dto.request.updatePlayer;

import com.code.fighters.entity.enums.UltimateSkill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdAllRequestDTO(
    @NotBlank String skinColor,
    @NotBlank String hairStyle,
    @NotBlank String hairColor,
    @NotBlank String eyeColor,
    @NotBlank String outfit,
    @NotBlank String outfitColor,
    @NotBlank String accessory,
    @NotNull UltimateSkill ultimateSkill
) {

}
