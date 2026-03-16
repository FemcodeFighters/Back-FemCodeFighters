package com.code.fighters.dto.request.updateUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdUserPassRequestDTO(
    @NotBlank(message = "Se requiere la contraseña actual")
    String currentPassword,

    @NotBlank(message = "Se requiere nueva contraseña")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 carácteres")
    String newPassword) {

    
}
