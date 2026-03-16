package com.code.fighters.mapper;

import com.code.fighters.dto.response.AuthResponseDTO;
import com.code.fighters.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    default AuthResponseDTO toDto(User user, String token) {
        return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getId());
    }
}