package com.code.fighters.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fighters.dto.request.updateUser.UpdUserEmailRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserNameRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserPassRequestDTO;
import com.code.fighters.dto.response.UserResponseDTO;
import com.code.fighters.entity.User;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.InvalidPasswordException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.exception.UserNotFoundException;
import com.code.fighters.mapper.UserMapper;
import com.code.fighters.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO getProfile(String email) {
        return userMapper.toDto(findByEmail(email));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDTO updateEmail(String email, UpdUserEmailRequestDTO request) {
        User user = findByEmail(email);

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        user.setEmail(request.email());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUsername(String email, UpdUserNameRequestDTO request) {
        User user = findByEmail(email);

        if (userRepository.existsByUsername(request.username())) {
            throw new UserNameAlreadyExistsException(request.username());
        }

        user.setUsername(request.username());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String email, UpdUserPassRequestDTO request) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        userRepository.delete(findByEmail(email));
    }

    // helper
    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}