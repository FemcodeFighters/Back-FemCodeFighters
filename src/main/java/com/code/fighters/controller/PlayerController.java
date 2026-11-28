package com.code.fighters.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.code.fighters.dto.response.UltimateConfigResponseDTO;
import com.code.fighters.service.PlayerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<PlayerResponseDTO> getCharacter(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(playerService.getCharacter(email));
    }

    @PatchMapping("/skin")
    public ResponseEntity<PlayerResponseDTO> updateSkinColor(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdSkinColorRequestDTO request) {
        return ResponseEntity.ok(playerService.updateSkinColor(email, request));
    }

    @PatchMapping("/hair")
    public ResponseEntity<PlayerResponseDTO> updateHair(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdHairRequestDTO request) {
        return ResponseEntity.ok(playerService.updateHair(email, request));
    }

    @PatchMapping("/eyes")
    public ResponseEntity<PlayerResponseDTO> updateEyeColor(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdEyeColorRequestDTO request) {
        return ResponseEntity.ok(playerService.updateEyeColor(email, request));
    }

    @PatchMapping("/outfit")
    public ResponseEntity<PlayerResponseDTO> updateOutfit(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdOutfitRequestDTO request) {
        return ResponseEntity.ok(playerService.updateOutfit(email, request));
    }

    @PatchMapping("/accessory")
    public ResponseEntity<PlayerResponseDTO> updateAccessory(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdAccessoryRequestDTO request) {
        return ResponseEntity.ok(playerService.updateAccessory(email, request));
    }

    @PatchMapping("/all")
    public ResponseEntity<PlayerResponseDTO> updateAll(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdAllRequestDTO request) {
        return ResponseEntity.ok(playerService.updateAll(email, request));
    }

    @PostMapping("/reset")
    public ResponseEntity<PlayerResponseDTO> resetCharacter(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(playerService.resetCharacter(email));
    }

    @PatchMapping("/ultimate")
    public ResponseEntity<PlayerResponseDTO> updateUltimateSkill(
            @AuthenticationPrincipal(expression = "username") String email,
            @Valid @RequestBody UpdUltimateRequestDTO request) {
        return ResponseEntity.ok(playerService.updateUltimateSkill(email, request));
    }

    @PostMapping("/use-ultimate")
    public ResponseEntity<PlayerResponseDTO> useUltimate(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(playerService.useUltimate(email));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerResponseDTO>> getRanking() {
        return ResponseEntity.ok(playerService.getRanking());
    }

    @PostMapping("/combat-result/{won}")
    public ResponseEntity<Void> updateCombatStats(
            @AuthenticationPrincipal(expression = "username") String email,
            @PathVariable boolean won) {
        playerService.updateStats(email, won);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ultimate-config")
    public ResponseEntity<UltimateConfigResponseDTO> getUltimateConfig(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(playerService.getUltimateConfig(email));
    }

    @GetMapping("/{playerId}/character")
    public ResponseEntity<PlayerResponseDTO> getCharacterById(@PathVariable Long playerId) {
        return playerService.getCharacterById(playerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}