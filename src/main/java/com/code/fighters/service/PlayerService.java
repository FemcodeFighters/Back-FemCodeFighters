package com.code.fighters.service;

import com.code.fighters.dto.request.updatePlayer.UpdAccessoryRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdEyeColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdHairRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdOutfitRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdSkinColorRequestDTO;
import com.code.fighters.dto.response.PlayerResponseDTO;

public interface PlayerService {
    PlayerResponseDTO getCharacter(String email);
    PlayerResponseDTO updateSkinColor(String email, UpdSkinColorRequestDTO request);
    PlayerResponseDTO updateHair(String email, UpdHairRequestDTO request);
    PlayerResponseDTO updateEyeColor(String email, UpdEyeColorRequestDTO request);
    PlayerResponseDTO updateOutfit(String email, UpdOutfitRequestDTO request);
    PlayerResponseDTO updateAccessory(String email, UpdAccessoryRequestDTO request);
    PlayerResponseDTO resetCharacter(String email);

}
