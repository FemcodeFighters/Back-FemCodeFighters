package com.code.fighters.dto.response;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id,
    String username,
    String email,
    LocalDateTime createdAt) {

}
