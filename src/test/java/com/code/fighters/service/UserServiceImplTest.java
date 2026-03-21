package com.code.fighters.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.code.fighters.dto.request.updateUser.*;
import com.code.fighters.dto.response.UserResponseDTO;
import com.code.fighters.entity.User;
import com.code.fighters.exception.EmailAlreadyExistsException;
import com.code.fighters.exception.InvalidPasswordException;
import com.code.fighters.exception.UserNameAlreadyExistsException;
import com.code.fighters.exception.UserNotFoundException;
import com.code.fighters.mapper.UserMapper;
import com.code.fighters.repository.UserRepository;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Tests Unitarios")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPass")
                .build();
        userDTO = new UserResponseDTO(1L, "testUser", "test@test.com", LocalDateTime.now());
    }

    @Nested
    @DisplayName("getProfile")
    class GetProfile {

        @Test
        @DisplayName("devuelve el perfil del usuario autenticado")
        void getProfile_returnsUserDTO() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(userDTO);
            UserResponseDTO result = userService.getProfile("test@test.com");
            assertThat(result.email()).isEqualTo("test@test.com");
            assertThat(result.username()).isEqualTo("testUser");
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("lanza UserNotFoundException si el email no existe")
        void getProfile_throwsIfNotFound() {
            when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.getProfile("noexiste@test.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("noexiste@test.com");
        }
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("devuelve la lista completa de usuarios mapeada")
        void getAllUsers_returnsMappedList() {
            when(userRepository.findAll()).thenReturn(List.of(user));
            when(userMapper.toDto(user)).thenReturn(userDTO);
            List<UserResponseDTO> result = userService.getAllUsers();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).username()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("devuelve lista vacía si no hay usuarios")
        void getAllUsers_returnsEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());
            assertThat(userService.getAllUsers()).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateEmail")
    class UpdateEmail {

        @Test
        @DisplayName("actualiza el email si no está en uso")
        void updateEmail_success() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
            when(userRepository.save(user)).thenAnswer(inv -> inv.getArgument(0));
            when(userMapper.toDto(user)).thenReturn(userDTO);
            userService.updateEmail("test@test.com", new UpdUserEmailRequestDTO("nuevo@test.com"));
            assertThat(user.getEmail()).isEqualTo("nuevo@test.com");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("lanza EmailAlreadyExistsException si el nuevo email ya está en uso")
        void updateEmail_throwsIfEmailTaken() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("otro@test.com")).thenReturn(true);
            assertThatThrownBy(
                    () -> userService.updateEmail("test@test.com", new UpdUserEmailRequestDTO("otro@test.com")))
                    .isInstanceOf(EmailAlreadyExistsException.class);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateUsername")
    class UpdateUsername {

        @Test
        @DisplayName("actualiza el username si no está en uso")
        void updateUsername_success() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("nuevoUser")).thenReturn(false);
            when(userRepository.save(user)).thenAnswer(inv -> inv.getArgument(0));
            when(userMapper.toDto(user)).thenReturn(userDTO);
            userService.updateUsername("test@test.com", new UpdUserNameRequestDTO("nuevoUser"));
            assertThat(user.getUsername()).isEqualTo("nuevoUser");
        }

        @Test
        @DisplayName("lanza UserNameAlreadyExistsException si el username ya existe")
        void updateUsername_throwsIfUsernameTaken() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("otraPersona")).thenReturn(true);
            assertThatThrownBy(
                    () -> userService.updateUsername("test@test.com", new UpdUserNameRequestDTO("otraPersona")))
                    .isInstanceOf(UserNameAlreadyExistsException.class);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("cambia la contraseña si la actual es correcta")
        void changePassword_success() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Password123!", "encodedPass")).thenReturn(true);
            when(passwordEncoder.encode("NuevaPass456!")).thenReturn("newEncodedPass");
            when(userRepository.save(user)).thenAnswer(inv -> inv.getArgument(0));
            assertThatNoException().isThrownBy(() -> userService.changePassword("test@test.com",
                    new UpdUserPassRequestDTO("Password123!", "NuevaPass456!")));
            assertThat(user.getPassword()).isEqualTo("newEncodedPass");
        }

        @Test
        @DisplayName("lanza InvalidPasswordException si la contraseña actual es incorrecta")
        void changePassword_throwsIfWrongPassword() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("WrongPass", "encodedPass")).thenReturn(false);
            assertThatThrownBy(() -> userService.changePassword("test@test.com",
                    new UpdUserPassRequestDTO("WrongPass", "NuevaPass456!")))
                    .isInstanceOf(InvalidPasswordException.class);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("la nueva contraseña se guarda encriptada")
        void changePassword_newPasswordIsEncoded() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Password123!", "encodedPass")).thenReturn(true);
            when(passwordEncoder.encode("NuevaPass456!")).thenReturn("$2a$newHash");
            when(userRepository.save(user)).thenAnswer(inv -> inv.getArgument(0));
            userService.changePassword("test@test.com",
                    new UpdUserPassRequestDTO("Password123!", "NuevaPass456!"));
            verify(passwordEncoder).encode("NuevaPass456!");
            assertThat(user.getPassword()).isEqualTo("$2a$newHash");
        }
    }

    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccount {

        @Test
        @DisplayName("elimina la cuenta del usuario correctamente")
        void deleteAccount_success() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
            userService.deleteAccount("test@test.com");
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("lanza UserNotFoundException si el usuario no existe")
        void deleteAccount_throwsIfNotFound() {
            when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.deleteAccount("noexiste@test.com"))
                    .isInstanceOf(UserNotFoundException.class);
            verify(userRepository, never()).delete(any());
        }
    }
}
