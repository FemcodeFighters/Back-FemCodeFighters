package com.code.fighters.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.code.fighters.entity.User;
import com.code.fighters.repository.UserRepository;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsConfig - Tests Unitarios")
class UserDetailsConfigTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserDetailsConfig userDetailsConfig;

    private UserDetailsService userDetailsService;
    private User user;

    @BeforeEach
    void setUp() {
        userDetailsService = userDetailsConfig.userDetailsService();
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPass")
                .build();
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("devuelve UserDetails con el email como username")
        void loadUser_usesEmailAsUsername() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            UserDetails result = userDetailsService.loadUserByUsername("test@test.com");
            assertThat(result.getUsername()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("devuelve UserDetails con la contraseña encriptada del usuario")
        void loadUser_usesEncodedPassword() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            UserDetails result = userDetailsService.loadUserByUsername("test@test.com");
            assertThat(result.getPassword()).isEqualTo("encodedPass");
        }

        @Test
        @DisplayName("devuelve UserDetails con el rol ROLE_USER")
        void loadUser_hasRoleUser() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            UserDetails result = userDetailsService.loadUserByUsername("test@test.com");
            assertThat(result.getAuthorities())
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        }

        @Test
        @DisplayName("la cuenta está habilitada y no expirada")
        void loadUser_accountIsActive() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            UserDetails result = userDetailsService.loadUserByUsername("test@test.com");
            assertThat(result.isEnabled()).isTrue();
            assertThat(result.isAccountNonExpired()).isTrue();
            assertThat(result.isAccountNonLocked()).isTrue();
            assertThat(result.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("lanza UsernameNotFoundException si el email no existe")
        void loadUser_throwsWhenEmailNotFound() {
            when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername("noexiste@test.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("noexiste@test.com");
        }

        @Test
        @DisplayName("consulta el repositorio por el email recibido")
        void loadUser_queriesRepositoryByEmail() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            userDetailsService.loadUserByUsername("test@test.com");
            verify(userRepository).findByEmail("test@test.com");
        }
    }
}
