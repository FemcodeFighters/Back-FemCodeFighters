package com.code.fighters.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.entity.Player;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    @Mapping(target = "username", source = "user.username")
    PlayerResponseDTO toDto(Player player);
}
