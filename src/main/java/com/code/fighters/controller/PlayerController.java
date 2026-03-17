package com.code.fighters.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code.fighters.dto.request.updatePlayer.UpdAccessoryRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdAllRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdEyeColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdHairRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdOutfitRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdSkinColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdUltimateRequestDTO;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.service.PlayerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
public ResponseEntity<PlayerResponseDTO> getCharacter(Principal principal) {
    // Principal siempre contiene el 'name' (username) si el token es válido
    if (principal == null) {
        return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(playerService.getCharacter(principal.getName()));
}

    @PatchMapping("/skin")
    public ResponseEntity<PlayerResponseDTO> updateSkinColor(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdSkinColorRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateSkinColor(userDetails.getUsername(), request));
    }

    @PatchMapping("/hair")
    public ResponseEntity<PlayerResponseDTO> updateHair(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdHairRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateHair(userDetails.getUsername(), request));
    }

    @PatchMapping("/eyes")
    public ResponseEntity<PlayerResponseDTO> updateEyeColor(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdEyeColorRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateEyeColor(userDetails.getUsername(), request));
    }

    @PatchMapping("/outfit")
    public ResponseEntity<PlayerResponseDTO> updateOutfit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdOutfitRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateOutfit(userDetails.getUsername(), request));
    }

    @PatchMapping("/accessory")
    public ResponseEntity<PlayerResponseDTO> updateAccessory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdAccessoryRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateAccessory(userDetails.getUsername(), request));
    }

    @PatchMapping("/all")
    public ResponseEntity<PlayerResponseDTO> updateAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdAllRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateAll(userDetails.getUsername(), request));
    }

    @PostMapping("/reset")
    public ResponseEntity<PlayerResponseDTO> resetCharacter(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(playerService.resetCharacter(userDetails.getUsername()));
    }

    @PatchMapping("/ultimate")
    public ResponseEntity<PlayerResponseDTO> updateUltimateSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdUltimateRequestDTO request) {
        return ResponseEntity.ok(
                playerService.updateUltimateSkill(userDetails.getUsername(), request));
    }

    @PostMapping("/use-ultimate")
    public ResponseEntity<PlayerResponseDTO> useUltimate(
            @AuthenticationPrincipal UserDetails userDetails) {
        PlayerResponseDTO updatedPlayer = playerService.useUltimate(userDetails.getUsername());
        return ResponseEntity.ok(updatedPlayer);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerResponseDTO>> getRanking() {
        return ResponseEntity.ok(playerService.getRanking());
    }

@PostMapping("/combat-result/{won}")
    public ResponseEntity<Void> updateCombatStats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable boolean won) {
        
        playerService.updateStats(userDetails.getUsername(), won);
        return ResponseEntity.ok().build();
    }
}
