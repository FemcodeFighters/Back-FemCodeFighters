package com.code.fighters.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.code.fighters.controller.AuthController;
import com.code.fighters.controller.HealthController;
import com.code.fighters.controller.PlayerController;
import com.code.fighters.controller.UserController;
import com.code.fighters.security.JwtAuthFilter;
import com.code.fighters.security.JwtService;
import com.code.fighters.service.AuthService;
import com.code.fighters.service.PlayerService;
import com.code.fighters.service.UserService;

@SuppressWarnings("null")
@WebMvcTest({ AuthController.class, HealthController.class, PlayerController.class, UserController.class })
@Import({ SecurityConfig.class, JwtAuthFilter.class })
@DisplayName("SecurityConfig - Tests de Configuración de Seguridad")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private PlayerService playerService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Nested
    @DisplayName("Rutas públicas - no requieren autenticación")
    class PublicRoutes {

        @Test
        @DisplayName("/api/auth/register es accesible sin token")
        void authRegister_isPublic() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                    .contentType("application/json")
                    .content("""
                            { "username": "u", "email": "e@e.com", "password": "p" }
                            """))
                    .andExpect(status().is(not(401)))
                    .andExpect(status().is(not(403)));
        }

        @Test
        @DisplayName("/api/auth/login es accesible sin token")
        void authLogin_isPublic() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                            { "email": "e@e.com", "password": "p" }
                            """))
                    .andExpect(status().is(not(401)))
                    .andExpect(status().is(not(403)));
        }

        @Test
        @DisplayName("/api/health es accesible sin token")
        void health_isPublic() throws Exception {
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Rutas protegidas - requieren autenticación")
    class ProtectedRoutes {

        @Test
        @DisplayName("/api/player/** devuelve 403 sin token")
        void playerRoutes_return403WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/player")).andExpect(status().isForbidden());
            mockMvc.perform(post("/api/player/reset")).andExpect(status().isForbidden());
            mockMvc.perform(post("/api/player/use-ultimate")).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/player/ranking")).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/player/ultimate-config")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("/api/users/** devuelve 403 sin token")
        void userRoutes_return403WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/users/profile")).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());
            mockMvc.perform(delete("/api/users")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("/api/player/** es accesible con autenticación")
        void playerRoutes_accessibleWhenAuthenticated() throws Exception {
            mockMvc.perform(get("/api/player/ranking"))
                    .andExpect(status().is(not(401)))
                    .andExpect(status().is(not(403)));
        }

        @Test
        @WithMockUser
        @DisplayName("/api/users/** es accesible con autenticación")
        void userRoutes_accessibleWhenAuthenticated() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().is(not(401)))
                    .andExpect(status().is(not(403)));
        }
    }

    @Nested
    @DisplayName("Sesión stateless - no crea HttpSession")
    class StatelessSession {

        @Test
        @WithMockUser
        @DisplayName("las respuestas no contienen cookie de sesión")
        void responses_doNotSetSessionCookie() throws Exception {
            mockMvc.perform(get("/api/health"))
                    .andExpect(cookie().doesNotExist("JSESSIONID"));
        }
    }

    @Nested
    @DisplayName("CORS - cabeceras de origen")
    class Cors {

        @Test
        @DisplayName("preflight OPTIONS desde origen permitido devuelve cabecera Allow-Origin")
        void cors_allowsPermittedOrigin() throws Exception {
            mockMvc.perform(options("/api/health")
                    .header("Origin", "http://localhost:5173")
                    .header("Access-Control-Request-Method", "GET"))
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        }

        @Test
        @DisplayName("preflight OPTIONS desde origen no permitido no devuelve cabecera Allow-Origin")
        void cors_blocksForbiddenOrigin() throws Exception {
            mockMvc.perform(options("/api/health")
                    .header("Origin", "http://evil.com")
                    .header("Access-Control-Request-Method", "GET"))
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }
    }
}
