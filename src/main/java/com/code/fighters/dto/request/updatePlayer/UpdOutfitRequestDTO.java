package com.code.fighters.dto.request.updatePlayer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdOutfitRequestDTO(
    @NotBlank(message = "Outfit is required")
    @Size(min = 2, max = 50, message = "Outfit must be between 2 and 50 characters")
    String outfit,

    @NotBlank(message = "Outfit color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid hex color format")
    String outfitColor
) {

}
