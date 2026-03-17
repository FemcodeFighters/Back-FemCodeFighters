package com.code.fighters.dto.request.updatePlayer;

import com.code.fighters.entity.enums.UltimateSkill;

import jakarta.validation.constraints.NotNull;

public record UpdUltimateRequestDTO(
    @NotNull(message = "La habilidad no puede estar vacía")
    UltimateSkill ultimateSkill) {

}
