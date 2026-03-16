package com.code.fighters.mapper;

import org.mapstruct.Mapper;

import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.entity.Player;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
PlayerResponseDTO toDto(Player player);
}
