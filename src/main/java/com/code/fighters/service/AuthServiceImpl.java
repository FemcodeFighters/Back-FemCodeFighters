package com.code.fighters.service;

import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fighters.dto.request.LoginRequestDTO;
import com.code.fighters.dto.request.RegisterRequestDTO;
import com.code.fighters.dto.response.AuthResponseDTO;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.exception.UserNotFoundException;
import com.code.fighters.mapper.AuthMapper;
import com.code.fighters.repository.PlayerRepository;
import com.code.fighters.repository.UserRepository;
import com.code.fighters.security.JwtService;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository     userRepository;
    private final PlayerRepository   playerRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtService         jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper         authMapper;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserNameAlreadyExistsException(request.username());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = Objects.requireNonNull(
                userRepository.save(user),
                "Error guardando usuario");

        playerRepository.save(Player.builder().user(savedUser).build());

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());
        return authMapper.toDto(savedUser, token);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return authMapper.toDto(user, token);
    }
}