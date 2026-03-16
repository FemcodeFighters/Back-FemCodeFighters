package com.code.fighters.dto.request.updateUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdUserNameRequestDTO(
    @NotBlank
    @Size(min = 2, max = 50, message = "El alias debe tener entre 2 y 50 cáracteres")
    String username
) {

}
