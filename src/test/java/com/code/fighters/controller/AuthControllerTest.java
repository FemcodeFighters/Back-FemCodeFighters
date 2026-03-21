package com.code.fighters.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.code.fighters.config.SecurityConfig;
import com.code.fighters.dto.response.AuthResponseDTO;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.security.JwtAuthFilter;
import com.code.fighters.security.JwtService;
import com.code.fighters.service.AuthService;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, JwtAuthFilter.class })
@DisplayName("AuthController - Tests de Endpoints")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;
  @MockitoBean
  private JwtService jwtService;
  @MockitoBean
  private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

  @Nested
  @DisplayName("POST /api/auth/register")
  class Register {

    @Test
    @DisplayName("201 - registro exitoso devuelve token y datos del usuario")
    void register_returns201WithToken() throws Exception {
      AuthResponseDTO response = new AuthResponseDTO("jwt-token", "testUser", "test@test.com", 1L);
      when(authService.register(any())).thenReturn(response);

      mockMvc.perform(post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "username": "testUser",
                "email": "test@test.com",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.token").value("jwt-token"))
          .andExpect(jsonPath("$.email").value("test@test.com"))
          .andExpect(jsonPath("$.username").value("testUser"))
          .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("409 - email ya registrado")
    void register_returns409WhenEmailExists() throws Exception {
      when(authService.register(any()))
          .thenThrow(new EmailAlreadyExistsException("test@test.com"));
      mockMvc.perform(post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "username": "testUser",
                "email": "test@test.com",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("409 - username ya en uso")
    void register_returns409WhenUsernameExists() throws Exception {
      when(authService.register(any()))
          .thenThrow(new UserNameAlreadyExistsException("testUser"));
      mockMvc.perform(post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "username": "testUser",
                "email": "test@test.com",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("400 - username demasiado corto (< 2 chars)")
    void register_returns400WhenUsernameTooShort() throws Exception {
      mockMvc.perform(post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "username": "a",
                "email": "test@test.com",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 - body vacío sin campos")
    void register_returns400WhenEmptyBody() throws Exception {
      mockMvc.perform(post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("{}"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /api/auth/login")
  class Login {

    @Test
    @DisplayName("200 - login correcto devuelve token")
    void login_returns200WithToken() throws Exception {
      AuthResponseDTO response = new AuthResponseDTO("jwt-token", "testUser", "test@test.com", 1L);
      when(authService.login(any())).thenReturn(response);
      mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "email": "test@test.com",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value("jwt-token"))
          .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("401 - credenciales incorrectas")
    void login_returns401WhenBadCredentials() throws Exception {
      when(authService.login(any()))
          .thenThrow(new BadCredentialsException("Bad credentials"));
      mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "email": "test@test.com",
                "password": "wrong"
              }
              """))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("400 - email con formato inválido")
    void login_returns400WhenInvalidEmail() throws Exception {
      mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "email": "no-es-un-email",
                "password": "Password123!"
              }
              """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 - contraseña en blanco")
    void login_returns400WhenBlankPassword() throws Exception {
      mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content("""
              {
                "email": "test@test.com",
                "password": ""
              }
              """))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /api/auth/logout")
  class Logout {

    @Test
    @DisplayName("200 - logout siempre OK (stateless JWT, sin invalidar token)")
    void logout_returns200() throws Exception {
      mockMvc.perform(post("/api/auth/logout"))
          .andExpect(status().isOk())
          .andExpect(content().string("Sesión cerrada correctamente"));
    }
  }
}
