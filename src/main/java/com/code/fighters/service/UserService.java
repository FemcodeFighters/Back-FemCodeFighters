package com.code.fighters.service;

import java.util.List;

import com.code.fighters.dto.request.updateUser.UpdUserEmailRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserNameRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserPassRequestDTO;
import com.code.fighters.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO getProfile(String email);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateEmail(String email, UpdUserEmailRequestDTO request);
    void changePassword(String email, UpdUserPassRequestDTO request);
    void deleteAccount(String email);
    UserResponseDTO updateUsername(String email, UpdUserNameRequestDTO request);
}
