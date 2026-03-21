package com.code.fighters.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("null")
@DisplayName("JwtService - Tests Unitarios")
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "test-secret-key-for-testing-purposes-only-must-be-long-enough-1234567890";
    private static final long EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("genera un token no nulo y no vacío")
        void generateToken_notNullOrEmpty() {
            String token = jwtService.generateToken(1L, "test@test.com");
            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("tokens distintos para usuarios distintos")
        void generateToken_differentUsersGetDifferentTokens() {
            String token1 = jwtService.generateToken(1L, "user1@test.com");
            String token2 = jwtService.generateToken(2L, "user2@test.com");
            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("el token tiene formato JWT (tres partes separadas por '.')")
        void generateToken_hasJwtFormat() {
            String token = jwtService.generateToken(1L, "test@test.com");
            assertThat(token.split("\\.")).hasSize(3);
        }
    }

    @Nested
    @DisplayName("extractEmail")
    class ExtractEmail {

        @Test
        @DisplayName("extrae el email exacto del subject del token")
        void extractEmail_returnsCorrectEmail() {
            String token = jwtService.generateToken(1L, "test@test.com");
            assertThat(jwtService.extractEmail(token)).isEqualTo("test@test.com");
        }
    }

    @Nested
    @DisplayName("extractUserId")
    class ExtractUserId {

        @Test
        @DisplayName("extrae el userId exacto del claim del token")
        void extractUserId_returnsCorrectId() {
            String token = jwtService.generateToken(42L, "test@test.com");
            assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("isValid")
    class IsValid {

        @Test
        @DisplayName("token recién generado es válido")
        void isValid_trueForFreshToken() {
            String token = jwtService.generateToken(1L, "test@test.com");
            assertThat(jwtService.isValid(token)).isTrue();
        }

        @Test
        @DisplayName("token con firma manipulada no es válido")
        void isValid_falseForTamperedToken() {
            String token = jwtService.generateToken(1L, "test@test.com");
            String tampered = token.substring(0, token.length() - 5) + "XXXXX";
            assertThat(jwtService.isValid(tampered)).isFalse();
        }

        @Test
        @DisplayName("cadena vacía no es válida")
        void isValid_falseForEmptyString() {
            assertThat(jwtService.isValid("")).isFalse();
        }

        @Test
        @DisplayName("token aleatorio no es válido")
        void isValid_falseForRandomString() {
            assertThat(jwtService.isValid("esto.no.es.un.jwt")).isFalse();
        }

        @Test
        @DisplayName("token expirado no es válido")
        void isValid_falseForExpiredToken() {
            JwtService expiredService = new JwtService();
            ReflectionTestUtils.setField(expiredService, "secret", SECRET);
            ReflectionTestUtils.setField(expiredService, "expiration", -1000L);
            String expiredToken = expiredService.generateToken(1L, "test@test.com");
            assertThat(jwtService.isValid(expiredToken)).isFalse();
        }

        @Test
        @DisplayName("token firmado con clave diferente no es válido")
        void isValid_falseForDifferentSecret() {
            JwtService otherService = new JwtService();
            ReflectionTestUtils.setField(otherService, "secret",
                    "otra-clave-totalmente-diferente-que-no-sirve-aqui-1234567890");
            ReflectionTestUtils.setField(otherService, "expiration", EXPIRATION);
            String foreignToken = otherService.generateToken(1L, "test@test.com");
            assertThat(jwtService.isValid(foreignToken)).isFalse();
        }
    }
}
