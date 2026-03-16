package com.code.fighters.service;

import com.code.fighters.dto.request.LoginRequestDTO;
import com.code.fighters.dto.request.RegisterRequestDTO;
import com.code.fighters.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}
