package com.code.fighters.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.code.fighters.config.GameBalanceConfig;
import com.code.fighters.dto.request.updatePlayer.*;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.dto.response.UltimateConfigResponseDTO;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.entity.enums.UltimateSkill;
import com.code.fighters.exception.PlayerNotFoundException;
import com.code.fighters.exception.UserNotFoundException;
import com.code.fighters.mapper.PlayerMapper;
import com.code.fighters.repository.PlayerRepository;
import com.code.fighters.repository.UserRepository;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerServiceImpl - Tests Unitarios")
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlayerMapper playerMapper;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private User user;
    private Player player;
    private PlayerResponseDTO playerResponseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPass")
                .build();

        player = Player.builder().user(user).build();
        playerResponseDTO = new PlayerResponseDTO(
                1L, "testUser", 0, 0,
                "#f5c5a3", "ponytail", "#7c3aed",
                "#2563eb", "hoodie", "#1e1b4b",
                "none", "FRIDAY_DEPLOY", null);
    }

    private void mockFindByEmail(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(playerRepository.findByUserId(user.getId())).thenReturn(Optional.of(player));
    }

    @Nested
    @DisplayName("getCharacter")
    class GetCharacter {

        @Test
        @DisplayName("devuelve el DTO del personaje del usuario autenticado")
        void getCharacter_returnsPlayerDTO() {
            mockFindByEmail("test@test.com");
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            PlayerResponseDTO result = playerService.getCharacter("test@test.com");
            assertThat(result.username()).isEqualTo("testUser");
            assertThat(result.ultimateSkill()).isEqualTo("FRIDAY_DEPLOY");
        }

        @Test
        @DisplayName("lanza UserNotFoundException si el email no existe")
        void getCharacter_throwsUserNotFound() {
            when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> playerService.getCharacter("noexiste@test.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("noexiste@test.com");
        }

        @Test
        @DisplayName("lanza PlayerNotFoundException si el usuario no tiene personaje")
        void getCharacter_throwsPlayerNotFound() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(playerRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
            assertThatThrownBy(() -> playerService.getCharacter("test@test.com"))
                    .isInstanceOf(PlayerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateSkinColor")
    class UpdateSkinColor {

        @Test
        @DisplayName("actualiza el campo skinColor en la entidad y llama a save")
        void updateSkinColor_updatesAndSaves() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateSkinColor("test@test.com", new UpdSkinColorRequestDTO("#ffffff"));
            assertThat(player.getSkinColor()).isEqualTo("#ffffff");
            verify(playerRepository).save(player);
        }

        @Test
        @DisplayName("actualiza updatedAt al guardar")
        void updateSkinColor_setsUpdatedAt() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateSkinColor("test@test.com", new UpdSkinColorRequestDTO("#ffffff"));
            assertThat(player.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("updateHair")
    class UpdateHair {

        @Test
        @DisplayName("actualiza hairStyle y hairColor a la vez")
        void updateHair_updatesBothFields() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateHair("test@test.com", new UpdHairRequestDTO("afro", "#000000"));
            assertThat(player.getHairStyle()).isEqualTo("afro");
            assertThat(player.getHairColor()).isEqualTo("#000000");
        }
    }

    @Nested
    @DisplayName("updateEyeColor")
    class UpdateEyeColor {

        @Test
        @DisplayName("actualiza el campo eyeColor")
        void updateEyeColor_updatesField() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateEyeColor("test@test.com", new UpdEyeColorRequestDTO("#ff0000"));
            assertThat(player.getEyeColor()).isEqualTo("#ff0000");
        }
    }

    @Nested
    @DisplayName("updateOutfit")
    class UpdateOutfit {

        @Test
        @DisplayName("actualiza outfit y outfitColor")
        void updateOutfit_updatesBothFields() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateOutfit("test@test.com", new UpdOutfitRequestDTO("jacket", "#111111"));
            assertThat(player.getOutfit()).isEqualTo("jacket");
            assertThat(player.getOutfitColor()).isEqualTo("#111111");
        }
    }

    @Nested
    @DisplayName("updateAccessory")
    class UpdateAccessory {

        @Test
        @DisplayName("actualiza el accesorio del personaje")
        void updateAccessory_updatesField() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.updateAccessory("test@test.com", new UpdAccessoryRequestDTO("glasses"));
            assertThat(player.getAccessory()).isEqualTo("glasses");
        }
    }

    @Nested
    @DisplayName("updateAll")
    class UpdateAll {

        @Test
        @DisplayName("actualiza todos los campos del personaje en una sola llamada")
        void updateAll_updatesAllFields() {
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            UpdAllRequestDTO request = new UpdAllRequestDTO(
                    "#aabbcc", "spiky", "#ff0000",
                    "#00ff00", "armor", "#0000ff",
                    "hat", UltimateSkill.GIT_CLONE);
            playerService.updateAll("test@test.com", request);
            assertThat(player.getSkinColor()).isEqualTo("#aabbcc");
            assertThat(player.getHairStyle()).isEqualTo("spiky");
            assertThat(player.getHairColor()).isEqualTo("#ff0000");
            assertThat(player.getEyeColor()).isEqualTo("#00ff00");
            assertThat(player.getOutfit()).isEqualTo("armor");
            assertThat(player.getOutfitColor()).isEqualTo("#0000ff");
            assertThat(player.getAccessory()).isEqualTo("hat");
            assertThat(player.getUltimateSkill()).isEqualTo(UltimateSkill.GIT_CLONE);
        }
    }

    @Nested
    @DisplayName("resetCharacter")
    class ResetCharacter {

        @Test
        @DisplayName("restaura todos los valores por defecto del personaje")
        void resetCharacter_restoresDefaults() {
            player.setSkinColor("#000000");
            player.setHairStyle("custom");
            player.setAccessory("sword");
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.resetCharacter("test@test.com");
            assertThat(player.getSkinColor()).isEqualTo("#f5c5a3");
            assertThat(player.getHairStyle()).isEqualTo("ponytail");
            assertThat(player.getHairColor()).isEqualTo("#7c3aed");
            assertThat(player.getEyeColor()).isEqualTo("#2563eb");
            assertThat(player.getOutfit()).isEqualTo("hoodie");
            assertThat(player.getOutfitColor()).isEqualTo("#1e1b4b");
            assertThat(player.getAccessory()).isEqualTo("none");
        }
    }

    @Nested
    @DisplayName("useUltimate - FRIDAY_DEPLOY")
    class UseUltimateFridayDeploy {

        @Test
        @DisplayName("cura al jugador la cantidad definida en GameBalanceConfig")
        void fridayDeploy_healsPlayer() {
            player.setHealth(60);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.useUltimate("test@test.com");
            assertThat(player.getHealth()).isEqualTo(Math.min(60 + GameBalanceConfig.FRIDAY_DEPLOY_HEAL, 100));
        }

        @Test
        @DisplayName("la salud nunca supera 100 al curar")
        void fridayDeploy_capsAt100Health() {
            player.setHealth(90);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.useUltimate("test@test.com");
            assertThat(player.getHealth()).isLessThanOrEqualTo(100);
        }

        @Test
        @DisplayName("lanza IllegalStateException si health = 0")
        void useUltimate_throwsIfDead() {
            player.setHealth(0);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            mockFindByEmail("test@test.com");
            assertThatThrownBy(() -> playerService.useUltimate("test@test.com"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("derrotado");
        }

        @Test
        @DisplayName("lanza RuntimeException si la habilidad está en cooldown (< 20s)")
        void useUltimate_throwsOnCooldown() {
            player.setHealth(100);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            player.setLastUltimateUsed(LocalDateTime.now().minusSeconds(5));
            mockFindByEmail("test@test.com");
            assertThatThrownBy(() -> playerService.useUltimate("test@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("enfriamiento");
        }

        @Test
        @DisplayName("funciona después de que el cooldown haya expirado (> 20s)")
        void useUltimate_worksAfterCooldownExpired() {
            player.setHealth(50);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            player.setLastUltimateUsed(LocalDateTime.now().minusSeconds(25));
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            assertThatNoException().isThrownBy(() -> playerService.useUltimate("test@test.com"));
        }

        @Test
        @DisplayName("funciona la primera vez (lastUltimateUsed = null)")
        void useUltimate_worksFirstTimeEver() {
            player.setHealth(50);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            player.setLastUltimateUsed(null);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            assertThatNoException().isThrownBy(() -> playerService.useUltimate("test@test.com"));
        }

        @Test
        @DisplayName("actualiza lastUltimateUsed tras usar la habilidad")
        void useUltimate_setsLastUltimateUsed() {
            player.setHealth(50);
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            player.setLastUltimateUsed(null);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.useUltimate("test@test.com");
            assertThat(player.getLastUltimateUsed()).isNotNull();
        }
    }

    @Nested
    @DisplayName("useUltimate - SPAGHETTI_CODE")
    class UseUltimateSpaghettiCode {

        @Test
        @DisplayName("inflige daño a todos los enemigos")
        void spaghettiCode_damagesAllEnemies() {
            player.setHealth(100);
            player.setUltimateSkill(UltimateSkill.SPAGHETTI_CODE);
            mockFindByEmail("test@test.com");
            Player enemy1 = Player.builder().user(User.builder().id(2L).email("e1@t.com").build()).build();
            Player enemy2 = Player.builder().user(User.builder().id(3L).email("e2@t.com").build()).build();
            enemy1.setHealth(100);
            enemy2.setHealth(100);
            when(playerRepository.findAllExcept(player.getId())).thenReturn(List.of(enemy1, enemy2));
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.useUltimate("test@test.com");
            int expectedHp = 100 - GameBalanceConfig.SPAGHETTI_TICK_DAMAGE;
            assertThat(enemy1.getHealth()).isEqualTo(expectedHp);
            assertThat(enemy2.getHealth()).isEqualTo(expectedHp);
        }

        @Test
        @DisplayName("la salud de los enemigos no baja de 0")
        void spaghettiCode_healthFloorIsZero() {
            player.setHealth(100);
            player.setUltimateSkill(UltimateSkill.SPAGHETTI_CODE);
            mockFindByEmail("test@test.com");
            Player enemy = Player.builder().user(User.builder().id(2L).email("e@t.com").build()).build();
            enemy.setHealth(1);
            when(playerRepository.findAllExcept(player.getId())).thenReturn(List.of(enemy));
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            playerService.useUltimate("test@test.com");
            assertThat(enemy.getHealth()).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("updateStats")
    class UpdateStats {

        @Test
        @DisplayName("incrementa wins al ganar")
        void updateStats_incrementsWins() {
            player.setWins(3);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            playerService.updateStats("test@test.com", true);
            assertThat(player.getWins()).isEqualTo(4);
            assertThat(player.getLosses()).isEqualTo(0);
        }

        @Test
        @DisplayName("incrementa losses al perder")
        void updateStats_incrementsLosses() {
            player.setLosses(1);
            mockFindByEmail("test@test.com");
            when(playerRepository.save(player)).thenAnswer(inv -> inv.getArgument(0));
            playerService.updateStats("test@test.com", false);
            assertThat(player.getLosses()).isEqualTo(2);
            assertThat(player.getWins()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getRanking")
    class GetRanking {

        @Test
        @DisplayName("devuelve la lista mapeada del top 10")
        void getRanking_returnsTop10() {
            when(playerRepository.findTop10ByOrderByWinsDesc()).thenReturn(List.of(player));
            when(playerMapper.toDto(player)).thenReturn(playerResponseDTO);
            List<PlayerResponseDTO> result = playerService.getRanking();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).username()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("devuelve lista vacía si no hay jugadores")
        void getRanking_returnsEmptyIfNoPlayers() {
            when(playerRepository.findTop10ByOrderByWinsDesc()).thenReturn(List.of());
            assertThat(playerService.getRanking()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUltimateConfig")
    class GetUltimateConfig {

        @Test
        @DisplayName("FRIDAY_DEPLOY - name, healAmount y grantsInvincibility correctos")
        void getUltimateConfig_fridayDeploy() {
            player.setUltimateSkill(UltimateSkill.FRIDAY_DEPLOY);
            mockFindByEmail("test@test.com");
            UltimateConfigResponseDTO config = playerService.getUltimateConfig("test@test.com");
            assertThat(config.name()).isEqualTo("FRIDAY_DEPLOY");
            assertThat(config.healAmount()).isEqualTo(GameBalanceConfig.FRIDAY_DEPLOY_HEAL);
            assertThat(config.grantsInvincibility()).isTrue();
            assertThat(config.tickDamage()).isEqualTo(0);
        }

        @Test
        @DisplayName("SPAGHETTI_CODE - tickDamage y tickCount correctos")
        void getUltimateConfig_spaghettiCode() {
            player.setUltimateSkill(UltimateSkill.SPAGHETTI_CODE);
            mockFindByEmail("test@test.com");
            UltimateConfigResponseDTO config = playerService.getUltimateConfig("test@test.com");
            assertThat(config.name()).isEqualTo("SPAGHETTI_CODE");
            assertThat(config.tickDamage()).isEqualTo(GameBalanceConfig.SPAGHETTI_TICK_DAMAGE);
            assertThat(config.tickCount()).isEqualTo(GameBalanceConfig.SPAGHETTI_TICK_COUNT);
            assertThat(config.healAmount()).isEqualTo(0);
            assertThat(config.grantsInvincibility()).isFalse();
        }

        @Test
        @DisplayName("GIT_CLONE - cloneDamage correcto")
        void getUltimateConfig_gitClone() {
            player.setUltimateSkill(UltimateSkill.GIT_CLONE);
            mockFindByEmail("test@test.com");
            UltimateConfigResponseDTO config = playerService.getUltimateConfig("test@test.com");
            assertThat(config.name()).isEqualTo("GIT_CLONE");
            assertThat(config.cloneDamage()).isEqualTo(GameBalanceConfig.GIT_CLONE_DAMAGE);
            assertThat(config.grantsInvincibility()).isFalse();
            assertThat(config.healAmount()).isEqualTo(0);
        }

        @Test
        @DisplayName("todos los skills tienen el cooldown global")
        void getUltimateConfig_allSkillsHaveGlobalCooldown() {
            for (UltimateSkill skill : UltimateSkill.values()) {
                player.setUltimateSkill(skill);
                when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
                when(playerRepository.findByUserId(user.getId())).thenReturn(Optional.of(player));
                UltimateConfigResponseDTO config = playerService.getUltimateConfig("test@test.com");
                assertThat(config.cooldownMs())
                        .as("Cooldown incorrecto para: " + skill)
                        .isEqualTo(GameBalanceConfig.GLOBAL_ULTIMATE_COOLDOWN_MS);
            }
        }
    }
}
