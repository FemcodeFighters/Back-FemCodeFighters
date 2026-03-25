package com.code.fighters.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerInput(
        @JsonProperty("playerId") String playerId,
        @JsonProperty("action") String action,
        @JsonProperty("tick") long tick) {
}