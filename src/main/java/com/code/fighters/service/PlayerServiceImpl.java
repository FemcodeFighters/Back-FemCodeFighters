package com.code.fighters.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
        return updateAndSave(email, p -> p.setSkinColor(request.skinColor()));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateHair(String email, UpdHairRequestDTO request) {
        return updateAndSave(email, p -> {
            p.setHairStyle(request.hairStyle());
            p.setHairColor(request.hairColor());
        });
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateEyeColor(String email, UpdEyeColorRequestDTO request) {
        return updateAndSave(email, p -> p.setEyeColor(request.eyeColor()));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateOutfit(String email, UpdOutfitRequestDTO request) {
        return updateAndSave(email, p -> {
            p.setOutfit(request.outfit());
            p.setOutfitColor(request.outfitColor());
        });
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateAccessory(String email, UpdAccessoryRequestDTO request) {
        return updateAndSave(email, p -> p.setAccessory(request.accessory()));
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateAll(String email, UpdAllRequestDTO request) {
        return updateAndSave(email, p -> {
            p.setSkinColor(request.skinColor());
            p.setHairStyle(request.hairStyle());
            p.setHairColor(request.hairColor());
            p.setEyeColor(request.eyeColor());
            p.setOutfit(request.outfit());
            p.setOutfitColor(request.outfitColor());
            p.setAccessory(request.accessory());
            p.setUltimateSkill(request.ultimateSkill());
        });
    }

    @Override
    @Transactional
    public PlayerResponseDTO resetCharacter(String email) {
        return updateAndSave(email, Player::resetAppearance);
    }

    @Override
    @Transactional
    public PlayerResponseDTO updateUltimateSkill(String email, UpdUltimateRequestDTO request) {
        return updateAndSave(email, p -> p.setUltimateSkill(request.ultimateSkill()));
    }

    @Override
    @Transactional
    public PlayerResponseDTO useUltimate(String email) {
        Player player = findByEmail(email);
        if (player.getHealth() <= 0) {
            throw new IllegalStateException("¡Un programador derrotado no puede picar código!");
        }
        long cooldownSeconds = GameBalanceConfig.GLOBAL_ULTIMATE_COOLDOWN_MS / 1000;
        if (player.getLastUltimateUsed() != null) {
            long secondsPassed = Duration.between(
                    player.getLastUltimateUsed(), LocalDateTime.now()).getSeconds();
            if (secondsPassed < cooldownSeconds) {
                throw new RuntimeException(
                        "Habilidad en enfriamiento. Faltan " + (cooldownSeconds - secondsPassed) + "s");
            }
        }

        switch (player.getUltimateSkill()) {
            case FRIDAY_DEPLOY -> applyFridayDeploy(player);
            case SPAGHETTI_CODE -> applySpaghettiStorm(playerRepository.findAllExcept(player.getId()));
            case GIT_CLONE -> {
                // El clon es solo visual: se dibuja en el cliente
            }
        }

        player.setLastUltimateUsed(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    @Override
    @Transactional
    public void updateStats(String email, boolean won) {
        updateAndSave(email, p -> {
            if (won) {
                p.setWins(p.getWins() + 1);
            } else {
                p.setLosses(p.getLosses() + 1);
            }
        });
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

    /** Patrón común de todas las actualizaciones: buscar, cambiar, marcar la fecha, guardar y devolver. */
    private PlayerResponseDTO updateAndSave(String email, Consumer<Player> changes) {
        Player player = findByEmail(email);
        changes.accept(player);
        player.setUpdatedAt(LocalDateTime.now());
        return playerMapper.toDto(playerRepository.save(player));
    }

    private Player findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return playerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new PlayerNotFoundException(email));
    }

    private void applyFridayDeploy(Player player) {
        int newHealth = Math.min(player.getHealth() + GameBalanceConfig.FRIDAY_DEPLOY_HEAL, Player.MAX_HEALTH);
        player.setHealth(newHealth);
    }

    private void applySpaghettiStorm(List<Player> enemies) {
        int damage = GameBalanceConfig.SPAGHETTI_TICK_DAMAGE;
        enemies.forEach(enemy -> enemy.setHealth(Math.max(enemy.getHealth() - damage, 0)));
    }
}