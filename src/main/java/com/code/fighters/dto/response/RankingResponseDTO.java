package com.code.fighters.dto.response;

public record RankingResponseDTO(
    String username,
    int wins,
    int losses,
    double winRate
) {

}
