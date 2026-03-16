package com.code.fighters.mapper;

import com.code.fighters.dto.response.UserResponseDTO;
import com.code.fighters.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDto(User user);
}