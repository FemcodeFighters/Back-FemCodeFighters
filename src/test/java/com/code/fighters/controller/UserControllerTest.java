package com.code.fighters.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.code.fighters.config.SecurityConfig;
import com.code.fighters.dto.response.UserResponseDTO;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.InvalidPasswordException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.security.JwtAuthFilter;
import com.code.fighters.security.JwtService;
import com.code.fighters.service.UserService;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@DisplayName("UserController - Tests de Endpoints")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService userService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private UserResponseDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserResponseDTO(1L, "testUser", "test@test.com", LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/users/profile")
    class GetProfile {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - devuelve el perfil del usuario autenticado")
        void getProfile_returns200() throws Exception {
            when(userService.getProfile("test@test.com")).thenReturn(userDTO);
            mockMvc.perform(get("/api/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testUser"))
                    .andExpect(jsonPath("$.email").value("test@test.com"))
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("401 - sin autenticación")
        void getProfile_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/users/profile"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @WithMockUser
        @DisplayName("200 - devuelve lista de usuarios")
        void getAllUsers_returns200() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(userDTO));
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].username").value("testUser"));
        }

        @Test
        @DisplayName("401 - sin autenticación")
        void getAllUsers_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/email")
    class UpdateEmail {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza el email correctamente")
        void updateEmail_returns200() throws Exception {
            when(userService.updateEmail(eq("test@test.com"), any())).thenReturn(userDTO);

            mockMvc.perform(patch("/api/users/email")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "email": "nuevo@test.com" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("409 - email ya en uso")
        void updateEmail_returns409WhenTaken() throws Exception {
            when(userService.updateEmail(eq("test@test.com"), any()))
                    .thenThrow(new EmailAlreadyExistsException("nuevo@test.com"));

            mockMvc.perform(patch("/api/users/email")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "email": "nuevo@test.com" }
                                    """))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - formato de email inválido")
        void updateEmail_returns400WhenInvalidFormat() throws Exception {
            mockMvc.perform(patch("/api/users/email")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "email": "no-es-un-email" }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - email en blanco")
        void updateEmail_returns400WhenBlank() throws Exception {
            mockMvc.perform(patch("/api/users/email")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "email": "" }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("401 - sin autenticación")
        void updateEmail_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(patch("/api/users/email")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "email": "nuevo@test.com" }
                                    """))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/username")
    class UpdateUsername {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("200 - actualiza el username correctamente")
        void updateUsername_returns200() throws Exception {
            when(userService.updateUsername(eq("test@test.com"), any())).thenReturn(userDTO);

            mockMvc.perform(patch("/api/users/username")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "username": "nuevoNick" }
                                    """))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("409 - username ya en uso")
        void updateUsername_returns409WhenTaken() throws Exception {
            when(userService.updateUsername(eq("test@test.com"), any()))
                    .thenThrow(new UserNameAlreadyExistsException("nuevoNick"));

            mockMvc.perform(patch("/api/users/username")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "username": "nuevoNick" }
                                    """))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - username demasiado corto (< 2 chars)")
        void updateUsername_returns400WhenTooShort() throws Exception {
            mockMvc.perform(patch("/api/users/username")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "username": "a" }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - username en blanco")
        void updateUsername_returns400WhenBlank() throws Exception {
            mockMvc.perform(patch("/api/users/username")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    { "username": "" }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/password")
    class ChangePassword {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("204 - cambia la contraseña correctamente")
        void changePassword_returns204() throws Exception {
            doNothing().when(userService).changePassword(eq("test@test.com"), any());

            mockMvc.perform(patch("/api/users/password")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                      "currentPassword": "Password123!",
                                      "newPassword": "NuevaPass456!"
                                    }
                                    """))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("401 - contraseña actual incorrecta")
        void changePassword_returns401WhenWrongPassword() throws Exception {
            doThrow(new InvalidPasswordException())
                    .when(userService).changePassword(eq("test@test.com"), any());

            mockMvc.perform(patch("/api/users/password")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                      "currentPassword": "wrong",
                                      "newPassword": "NuevaPass456!"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - nueva contraseña demasiado corta (< 6 chars)")
        void changePassword_returns400WhenNewPasswordTooShort() throws Exception {
            mockMvc.perform(patch("/api/users/password")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                      "currentPassword": "Password123!",
                                      "newPassword": "abc"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("400 - campos en blanco")
        void changePassword_returns400WhenBlankFields() throws Exception {
            mockMvc.perform(patch("/api/users/password")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                      "currentPassword": "",
                                      "newPassword": ""
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("401 - sin autenticación")
        void changePassword_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(patch("/api/users/password")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                      "currentPassword": "Password123!",
                                      "newPassword": "NuevaPass456!"
                                    }
                                    """))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users")
    class DeleteAccount {

        @Test
        @WithMockUser(username = "test@test.com")
        @DisplayName("204 - elimina la cuenta y devuelve sin contenido")
        void deleteAccount_returns204() throws Exception {
            doNothing().when(userService).deleteAccount("test@test.com");
            mockMvc.perform(delete("/api/users"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("401 - sin autenticación no puede eliminar")
        void deleteAccount_returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(delete("/api/users"))
                    .andExpect(status().isForbidden());
        }
    }
}
