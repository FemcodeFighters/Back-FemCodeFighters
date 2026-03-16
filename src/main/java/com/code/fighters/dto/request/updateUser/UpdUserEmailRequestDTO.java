package com.code.fighters.dto.request.updateUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdUserEmailRequestDTO(
    @NotBlank(message = "Se requiere un Email")
    @Email(message = "Formato inválido")
    String email
) {}