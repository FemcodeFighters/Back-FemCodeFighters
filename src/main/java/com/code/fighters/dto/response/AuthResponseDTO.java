package com.code.fighters.dto.response;

public record AuthResponseDTO(
    String token,
    String username,
    String email,
    Long userId) {

    
}
