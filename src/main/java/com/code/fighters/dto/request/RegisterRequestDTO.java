package com.code.fighters.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
    @NotBlank(message = "Es necesario un alias")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 carácteres")
    String username,

    @NotBlank(message = "Se requiere Email")
    String email,

    @NotBlank(message = "Se requiere Contraseña")
    String password
) {}