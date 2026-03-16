package com.code.fighters.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    int status,
    String error,
    String path,
    LocalDateTime timestamp
) {

}
