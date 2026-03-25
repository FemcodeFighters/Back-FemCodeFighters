package com.code.fighters.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fighters.config.GameBalanceConfig;
import com.code.fighters.dto.request.updatePlayer.UpdAccessoryRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdAllRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdEyeColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdHairRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdOutfitRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdSkinColorRequestDTO;
import com.code.fighters.dto.request.updatePlayer.UpdUltimateRequestDTO;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.dto.response.UltimateConfigResponseDTO;
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
    @Transactional(readOnly = true)
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
    public PlayerResponseDTO updateAll(String email, UpdAllRequestDTO request) {
        Player player = findByEmail(email);
        player.setSkinColor(request.skinColor());
        player.setHairStyle(request.hairStyle());
        player.setHairColor(request.hairColor());
        player.setEyeColor(request.eyeColor());
        player.setOutfit(request.outfit());
        player.setOutfitColor(request.outfitColor());
        player.setAccessory(request.accessory());
        player.setUltimateSkill(request.ultimateSkill());
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

    private Player findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return playerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new PlayerNotFoundException(email));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateUltimateSkill(String email, UpdUltimateRequestDTO request) {
        Player player = findByEmail(email);
        player.setUltimateSkill(request.ultimateSkill());
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponseDTO useUltimate(String email) {
        Player player = findByEmail(email);
        if (player.getHealth() <= 0) {
            throw new IllegalStateException("¡Un programador derrotado no puede picar código!");
        }
        long cooldownSeconds = 20;
        if (player.getLastUltimateUsed() != null) {
            long secondsPassed = java.time.Duration.between(
                    player.getLastUltimateUsed(),
                    java.time.LocalDateTime.now()).getSeconds();

            if (secondsPassed < cooldownSeconds) {
                throw new RuntimeException(
                        "Habilidad en enfriamiento. Faltan " + (cooldownSeconds - secondsPassed) + "s");
            }
        }

        switch (player.getUltimateSkill()) {
            case FRIDAY_DEPLOY -> applyFridayDeploy(player);
            case SPAGHETTI_CODE -> {
                List<Player> enemies = playerRepository.findAllExcept(player.getId());
                applySpaghettiStorm(enemies);
            }
            case GIT_CLONE -> applyGitClone(player);
        }

        player.setLastUltimateUsed(java.time.LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    private void applyFridayDeploy(Player player) {
        int newHealth = Math.min(player.getHealth() + GameBalanceConfig.FRIDAY_DEPLOY_HEAL, 100);
        player.setHealth(newHealth);
    }

    private void applySpaghettiStorm(List<Player> enemies) {
        int damage = GameBalanceConfig.SPAGHETTI_TICK_DAMAGE;

        enemies.forEach(enemy -> {
            int newHealth = Math.max(enemy.getHealth() - damage, 0);
            enemy.setHealth(newHealth);
        });
    }

    private void applyGitClone(Player owner) {
        long lifespan = GameBalanceConfig.CLONE_LIFESPAN_MS;
        LocalDateTime expirationTime = LocalDateTime.now().plusNanos(lifespan * 1_000_000);

        System.out.println("Invocando clon para: " + owner.getUser().getEmail() + " hasta " + expirationTime);

    }

    @Override
    @Transactional
    public void updateStats(String email, boolean won) {
        Player player = findByEmail(email);

        if (won) {
            player.setWins(player.getWins() + 1);
        } else {
            player.setLosses(player.getLosses() + 1);
        }

        player.setUpdatedAt(LocalDateTime.now());
        playerRepository.save(player);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDTO> getRanking() {
        return playerRepository.findTop10ByOrderByWinsDesc()
                .stream()
                .map(playerMapper::toDto)
                .toList();
    }

    @Override
    public UltimateConfigResponseDTO getUltimateConfig(String username) {
        Player player = findByEmail(username);
        return switch (player.getUltimateSkill()) {
            case FRIDAY_DEPLOY -> new UltimateConfigResponseDTO(
                    "FRIDAY_DEPLOY",
                    GameBalanceConfig.FRIDAY_DEPLOY_HEAL,
                    true, 3000,
                    0, 0, 0, 0,
                    GameBalanceConfig.GLOBAL_ULTIMATE_COOLDOWN_MS);
            case SPAGHETTI_CODE -> new UltimateConfigResponseDTO(
                    "SPAGHETTI_CODE",
                    0, false, 0,
                    GameBalanceConfig.SPAGHETTI_TICK_DAMAGE,
                    GameBalanceConfig.SPAGHETTI_TICK_COUNT,
                    GameBalanceConfig.SPAGHETTI_TICK_INTERVAL_MS,
                    0,
                    GameBalanceConfig.GLOBAL_ULTIMATE_COOLDOWN_MS);
            case GIT_CLONE -> new UltimateConfigResponseDTO(
                    "GIT_CLONE",
                    0, false, 0,
                    0, 0, 0,
                    GameBalanceConfig.GIT_CLONE_DAMAGE,
                    GameBalanceConfig.GLOBAL_ULTIMATE_COOLDOWN_MS);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerResponseDTO> getCharacterById(Long playerId) {
    return playerRepository.findById(playerId)
            .map(playerMapper::toDto);
}
}