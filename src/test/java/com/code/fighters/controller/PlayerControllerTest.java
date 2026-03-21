package com.code.fighters.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.code.fighters.config.SecurityConfig;
import com.code.fighters.dto.response.PlayerResponseDTO;
import com.code.fighters.dto.response.UltimateConfigResponseDTO;
import com.code.fighters.security.JwtAuthFilter;
import com.code.fighters.security.JwtService;
import com.code.fighters.service.PlayerService;

@WebMvcTest(PlayerController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@DisplayName("PlayerController - Tests de Endpoints")
class PlayerControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PlayerService playerService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private PlayerResponseDTO playerDTO;

    @BeforeEach
    void setUp() {
        playerDTO = new PlayerResponseDTO(
                1L, "testUser", 5, 2,
                "#f5c5a3", "ponytail", "#7c3aed",
                "#2563eb", "hoodie", "#1e1b4b",
                "none", "FRIDAY_DEPLOY", null
        );
    }

    @Nested
    @DisplayName("GET /api/player")
    class GetCharacter {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - devuelve el personaje con sus campos correctos")
        void getCharacter_returns200() throws Exception {
            when(playerService.getCharacter("test@test.com")).thenReturn(playerDTO);
            mockMvc.perform(get("/api/player"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testUser"))
                    .andExpect(jsonPath("$.wins").value(5))
                    .andExpect(jsonPath("$.losses").value(2))
                    .andExpect(jsonPath("$.skinColor").value("#f5c5a3"))
                    .andExpect(jsonPath("$.ultimateSkill").value("FRIDAY_DEPLOY"));
        }

        @Test
        @DisplayName("401 - sin autenticación")
        void getCharacter_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/player"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/skin")
    class UpdateSkinColor {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza el color de piel con hex válido")
        void updateSkinColor_returns200() throws Exception {
            when(playerService.updateSkinColor(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/skin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "skinColor": "#ffffff" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - hex inválido no pasa la validación @Pattern")
        void updateSkinColor_returns400WhenInvalidHex() throws Exception {
            mockMvc.perform(patch("/api/player/skin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "skinColor": "notahex" }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - campo skinColor vacío")
        void updateSkinColor_returns400WhenBlank() throws Exception {
            mockMvc.perform(patch("/api/player/skin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "skinColor": "" }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/hair")
    class UpdateHair {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza pelo con datos válidos")
        void updateHair_returns200() throws Exception {
            when(playerService.updateHair(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/hair")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "hairStyle": "afro", "hairColor": "#000000" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - hairColor con formato hex inválido")
        void updateHair_returns400WhenInvalidHairColor() throws Exception {
            mockMvc.perform(patch("/api/player/hair")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "hairStyle": "afro", "hairColor": "rojo" }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/eyes")
    class UpdateEyeColor {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza color de ojos")
        void updateEyeColor_returns200() throws Exception {
            when(playerService.updateEyeColor(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/eyes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "eyeColor": "#00ff00" }
                                    """))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/outfit")
    class UpdateOutfit {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza outfit con datos válidos")
        void updateOutfit_returns200() throws Exception {
            when(playerService.updateOutfit(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/outfit")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "outfit": "armor", "outfitColor": "#333333" }
                                    """))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/accessory")
    class UpdateAccessory {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza accesorio")
        void updateAccessory_returns200() throws Exception {
            when(playerService.updateAccessory(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/accessory")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "accessory": "glasses" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - accesorio vacío no pasa @NotBlank")
        void updateAccessory_returns400WhenBlank() throws Exception {
            mockMvc.perform(patch("/api/player/accessory")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "accessory": "" }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/player/reset")
    class ResetCharacter {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - resetea el personaje a valores por defecto")
        void resetCharacter_returns200() throws Exception {
            when(playerService.resetCharacter("test@test.com")).thenReturn(playerDTO);
            mockMvc.perform(post("/api/player/reset"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.skinColor").value("#f5c5a3"));
        }

        @Test
        @DisplayName("401 - sin autenticación no puede resetear")
        void resetCharacter_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/api/player/reset"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/player/ultimate")
    class UpdateUltimateSkill {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza la habilidad ultimate")
        void updateUltimate_returns200() throws Exception {
            when(playerService.updateUltimateSkill(eq("test@test.com"), any())).thenReturn(playerDTO);
            mockMvc.perform(patch("/api/player/ultimate")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "ultimateSkill": "GIT_CLONE" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - ultimateSkill null no pasa @NotNull")
        void updateUltimate_returns400WhenNull() throws Exception {
            mockMvc.perform(patch("/api/player/ultimate")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "ultimateSkill": null }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/player/use-ultimate")
    class UseUltimate {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - usa la habilidad ultimate correctamente")
        void useUltimate_returns200() throws Exception {
            when(playerService.useUltimate("test@test.com")).thenReturn(playerDTO);
            mockMvc.perform(post("/api/player/use-ultimate"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - cooldown activo lanza error")
        void useUltimate_returns400OnCooldown() throws Exception {
            when(playerService.useUltimate("test@test.com"))
                    .thenThrow(new RuntimeException("Habilidad en enfriamiento. Faltan 15s"));
            mockMvc.perform(post("/api/player/use-ultimate"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - jugador derrotado no puede usar habilidades")
        void useUltimate_returns400WhenDead() throws Exception {
            when(playerService.useUltimate("test@test.com"))
                    .thenThrow(new IllegalStateException("¡Un programador derrotado no puede picar código!"));
            mockMvc.perform(post("/api/player/use-ultimate"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/player/ranking")
    class GetRanking {

        @Test
        @WithMockUser
        @DisplayName("200 - devuelve array de jugadores ordenado")
        void getRanking_returns200() throws Exception {
            when(playerService.getRanking()).thenReturn(List.of(playerDTO));
            mockMvc.perform(get("/api/player/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].username").value("testUser"))
                    .andExpect(jsonPath("$[0].wins").value(5));
        }
    }

    @Nested
    @DisplayName("POST /api/player/combat-result/{won}")
    class CombatResult {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - registra victoria")
        void combatResult_won() throws Exception {
            doNothing().when(playerService).updateStats("test@test.com", true);
            mockMvc.perform(post("/api/player/combat-result/true"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - registra derrota")
        void combatResult_lost() throws Exception {
            doNothing().when(playerService).updateStats("test@test.com", false);
            mockMvc.perform(post("/api/player/combat-result/false"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/player/ultimate-config")
    class UltimateConfig {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - devuelve la configuración con el campo 'name' correcto")
        void ultimateConfig_returns200() throws Exception {
            UltimateConfigResponseDTO config = new UltimateConfigResponseDTO(
                    "FRIDAY_DEPLOY", 30, true, 3000,
                    0, 0, 0, 0, 20000
            );
            when(playerService.getUltimateConfig("test@test.com")).thenReturn(config);
            mockMvc.perform(get("/api/player/ultimate-config"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("FRIDAY_DEPLOY"))
                    .andExpect(jsonPath("$.healAmount").value(30))
                    .andExpect(jsonPath("$.grantsInvincibility").value(true))
                    .andExpect(jsonPath("$.cooldownMs").value(20000));
        }
    }
}
