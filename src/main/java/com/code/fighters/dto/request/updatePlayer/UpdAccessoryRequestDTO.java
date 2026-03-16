package com.code.fighters.dto.request.updatePlayer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdAccessoryRequestDTO(
    @NotBlank(message = "Accessory is required")
    @Size(min = 2, max = 50, message = "Accessory must be between 2 and 50 characters")
    String accessory
) {

}
