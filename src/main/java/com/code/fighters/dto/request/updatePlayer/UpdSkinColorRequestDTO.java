package com.code.fighters.dto.request.updatePlayer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdSkinColorRequestDTO(
    @NotBlank(message = "Skin color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid hex color format")
    String skinColor) {

}
