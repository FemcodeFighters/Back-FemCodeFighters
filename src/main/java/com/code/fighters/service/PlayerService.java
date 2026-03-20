package com.code.fighters.service;

import java.util.List;

import com.code.fighters.dto.request.updatePlayer.UpdAccessoryRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdAllRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdEyeColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdHairRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdOutfitRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdSkinColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdUltimateRequestDTO;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.dto.response.UltimateConfigResponseDTO;

public interface PlayerService {
    PlayerResponseDTO getCharacter(String email);
    PlayerResponseDTO updateSkinColor(String email, UpdSkinColorRequestDTO request);
    PlayerResponseDTO updateHair(String email, UpdHairRequestDTO request);
    PlayerResponseDTO updateEyeColor(String email, UpdEyeColorRequestDTO request);
    PlayerResponseDTO updateOutfit(String email, UpdOutfitRequestDTO request);
    PlayerResponseDTO updateAccessory(String email, UpdAccessoryRequestDTO request);
    PlayerResponseDTO updateAll(String email, UpdAllRequestDTO request);
    PlayerResponseDTO resetCharacter(String email);
    PlayerResponseDTO updateUltimateSkill(String email, UpdUltimateRequestDTO request);
    PlayerResponseDTO useUltimate(String email);
    void updateStats(String email, boolean won);
    List<PlayerResponseDTO> getRanking();
    public UltimateConfigResponseDTO getUltimateConfig(String username);
}
