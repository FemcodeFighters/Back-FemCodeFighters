package com.code.fighters.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fighters.dto.request.updatePlayer.UpdAccessoryRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdEyeColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdHairRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdOutfitRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdSkinColorRequestDTO;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.exception.PlayerNotFoundException;
import com.code.fighters.exception.UserNotFoundException;
import com.code.fighters.mapper.PlayerMapper;
import com.code.fighters.repository.PlayerRepository;
import com.code.fighters.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final PlayerMapper playerMapper;

    @Override
    public PlayerResponseDTO getCharacter(String email) {
        return playerMapper.toDto(findByEmail(email));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateSkinColor(String email, UpdSkinColorRequestDTO request) {
        Player player = findByEmail(email);
        player.setSkinColor(request.skinColor());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateHair(String email, UpdHairRequestDTO request) {
        Player player = findByEmail(email);
        player.setHairStyle(request.hairStyle());
        player.setHairColor(request.hairColor());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateEyeColor(String email, UpdEyeColorRequestDTO request) {
        Player player = findByEmail(email);
        player.setEyeColor(request.eyeColor());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateOutfit(String email, UpdOutfitRequestDTO request) {
        Player player = findByEmail(email);
        player.setOutfit(request.outfit());
        player.setOutfitColor(request.outfitColor());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateAccessory(String email, UpdAccessoryRequestDTO request) {
        Player player = findByEmail(email);
        player.setAccessory(request.accessory());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO resetCharacter(String email) {
        Player player = findByEmail(email);
        player.setSkinColor("#f5c5a3");
        player.setHairStyle("ponytail");
        player.setHairColor("#7c3aed");
        player.setEyeColor("#2563eb");
        player.setOutfit("hoodie");
        player.setOutfitColor("#1e1b4b");
        player.setAccessory("none");
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Player findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return playerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new PlayerNotFoundException(email));
    }
}