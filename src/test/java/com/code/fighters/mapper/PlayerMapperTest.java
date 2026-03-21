package com.code.fighters.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.entity.enums.UltimateSkill;

import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PlayerMapper - Tests de Mapeo")
class PlayerMapperTest {

    @Autowired private PlayerMapper playerMapper;

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("mapea user.username al campo plano username del DTO")
        void toDto_mapsNestedUsernameToFlatField() {
            User user = User.builder()
                    .id(1L)
                    .username("testUser")
                    .email("test@test.com")
                    .password("pass")
                    .build();
            Player player = Player.builder().user(user).build();
            PlayerResponseDTO dto = playerMapper.toDto(player);
            assertThat(dto.username()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("mapea todos los campos de apariencia correctamente")
        void toDto_mapsAppearanceFields() {
            User user = User.builder()
                    .id(1L)
                    .username("testUser")
                    .email("test@test.com")
                    .password("pass")
                    .build();
            Player player = Player.builder()
                    .user(user)
                    .build();
            player.setSkinColor("#aabbcc");
            player.setHairStyle("afro");
            player.setHairColor("#ff0000");
            player.setEyeColor("#00ff00");
            player.setOutfit("armor");
            player.setOutfitColor("#0000ff");
            player.setAccessory("glasses");
            PlayerResponseDTO dto = playerMapper.toDto(player);
            assertThat(dto.skinColor()).isEqualTo("#aabbcc");
            assertThat(dto.hairStyle()).isEqualTo("afro");
            assertThat(dto.hairColor()).isEqualTo("#ff0000");
            assertThat(dto.eyeColor()).isEqualTo("#00ff00");
            assertThat(dto.outfit()).isEqualTo("armor");
            assertThat(dto.outfitColor()).isEqualTo("#0000ff");
            assertThat(dto.accessory()).isEqualTo("glasses");
        }

        @Test
        @DisplayName("mapea wins, losses y ultimateSkill correctamente")
        void toDto_mapsStatsAndSkill() {
            User user = User.builder()
                    .id(1L)
                    .username("testUser")
                    .email("test@test.com")
                    .password("pass")
                    .build();
            Player player = Player.builder()
                    .user(user)
                    .build();
            player.setWins(10);
            player.setLosses(3);
            player.setUltimateSkill(UltimateSkill.GIT_CLONE);
            PlayerResponseDTO dto = playerMapper.toDto(player);
            assertThat(dto.wins()).isEqualTo(10);
            assertThat(dto.losses()).isEqualTo(3);
            assertThat(dto.ultimateSkill()).isEqualTo("GIT_CLONE");
        }

        @Test
        @DisplayName("mapea updatedAt correctamente")
        void toDto_mapsUpdatedAt() {
            User user = User.builder()
                    .id(1L)
                    .username("testUser")
                    .email("test@test.com")
                    .password("pass")
                    .build();
            LocalDateTime now = LocalDateTime.now();
            Player player = Player.builder().user(user).build();
            player.setUpdatedAt(now);
            PlayerResponseDTO dto = playerMapper.toDto(player);
            assertThat(dto.updatedAt()).isEqualTo(now);
        }
    }
}
