package com.code.fighters.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.code.fighters.dto.request.LoginRequestDTO;
import com.code.fighters.dto.request.RegisterRequestDTO;
import com.code.fighters.dto.response.AuthResponseDTO;
import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.mapper.AuthMapper;
import com.code.fighters.repository.PlayerRepository;
import com.code.fighters.repository.UserRepository;
import com.code.fighters.security.JwtService;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl - Tests Unitarios")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User savedUser;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        savedUser = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPass")
                .build();

        authResponseDTO = new AuthResponseDTO("jwt-token", "testUser", "test@test.com", 1L);
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("registro exitoso: crea usuario, crea personaje y devuelve token")
        void register_success() {
            RegisterRequestDTO request = new RegisterRequestDTO("testUser", "test@test.com", "Password123!");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.existsByUsername(request.username())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn("jwt-token");
            when(authMapper.toDto(savedUser, "jwt-token")).thenReturn(authResponseDTO);
            AuthResponseDTO result = authService.register(request);
            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.email()).isEqualTo("test@test.com");
            assertThat(result.username()).isEqualTo("testUser");
            assertThat(result.userId()).isEqualTo(1L);
            verify(playerRepository).save(any(Player.class));
        }

        @Test
        @DisplayName("lanza EmailAlreadyExistsException si el email ya está registrado")
        void register_throwsWhenEmailExists() {
            RegisterRequestDTO request = new RegisterRequestDTO("testUser", "test@test.com", "Password123!");
            when(userRepository.existsByEmail(request.email())).thenReturn(true);
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("test@test.com");
            verify(userRepository, never()).save(any());
            verify(playerRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza UserNameAlreadyExistsException si el username ya está en uso")
        void register_throwsWhenUsernameExists() {
            RegisterRequestDTO request = new RegisterRequestDTO("testUser", "test@test.com", "Password123!");
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.existsByUsername(request.username())).thenReturn(true);
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(UserNameAlreadyExistsException.class)
                    .hasMessageContaining("testUser");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("la contraseña se guarda encriptada, nunca en texto plano")
        void register_passwordIsEncoded() {
            RegisterRequestDTO request = new RegisterRequestDTO("testUser", "test@test.com", "Password123!");
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(passwordEncoder.encode("Password123!")).thenReturn("$2a$hash");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(playerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken(any(), any())).thenReturn("token");
            when(authMapper.toDto(any(), any())).thenReturn(authResponseDTO);

            authService.register(request);

            verify(passwordEncoder).encode("Password123!");
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isNotEqualTo("Password123!");
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("login correcto devuelve token JWT con datos del usuario")
        void login_success() {
            LoginRequestDTO request = new LoginRequestDTO("test@test.com", "Password123!");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null);
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(savedUser));
            when(jwtService.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn("jwt-token");
            when(authMapper.toDto(savedUser, "jwt-token")).thenReturn(authResponseDTO);
            AuthResponseDTO result = authService.login(request);
            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.userId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("lanza BadCredentialsException con credenciales incorrectas")
        void login_throwsOnBadCredentials() {
            LoginRequestDTO request = new LoginRequestDTO("test@test.com", "wrong");
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("se invoca el AuthenticationManager con email y password")
        void login_invokesAuthenticationManager() {
            LoginRequestDTO request = new LoginRequestDTO("test@test.com", "Password123!");
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(userRepository.findByEmail(any())).thenReturn(Optional.of(savedUser));
            when(jwtService.generateToken(any(), any())).thenReturn("token");
            when(authMapper.toDto(any(), any())).thenReturn(authResponseDTO);
            authService.login(request);
            verify(authenticationManager).authenticate(
                    argThat(auth -> auth.getPrincipal().equals("test@test.com") &&
                            auth.getCredentials().equals("Password123!")));
        }
    }
}
