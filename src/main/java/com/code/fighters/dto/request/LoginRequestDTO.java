package com.code.fighters.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
     @NotBlank(message = "Email requerido") 
     @Email(message = "Email no válido")
    String email,

    @NotBlank(message = "Se requiere contraseña")
    String password
) {

}
