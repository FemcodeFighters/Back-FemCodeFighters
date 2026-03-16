package com.code.fighters.dto.request.updatePlayer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdHairRequestDTO(
    @NotBlank(message = "Hair style is required")
    @Size(min = 2, max = 50, message = "Hair style must be between 2 and 50 characters")
    String hairStyle,

    @NotBlank(message = "Hair color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid hex color format")
    String hairColor
) {

}
