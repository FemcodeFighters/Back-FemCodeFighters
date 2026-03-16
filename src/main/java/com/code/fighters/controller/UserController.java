package com.code.fighters.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code.fighters.dto.request.updateUser.UpdUserEmailRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserNameRequestDTO;
import com.code.fighters.dto.request.updateUser.UpdUserPassRequestDTO;
import com.code.fighters.dto.response.UserResponseDTO;
import com.code.fighters.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/email")
    public ResponseEntity<UserResponseDTO> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdUserEmailRequestDTO request) {
        return ResponseEntity.ok(
            userService.updateEmail(userDetails.getUsername(), request)
        );
    }

    @PatchMapping("/username")
    public ResponseEntity<UserResponseDTO> updateUsername(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdUserNameRequestDTO request) {
        return ResponseEntity.ok(
            userService.updateUsername(userDetails.getUsername(), request)
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdUserPassRequestDTO request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
