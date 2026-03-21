package com.code.fighters.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.code.fighters.entity.Player;
import com.code.fighters.entity.User;
import com.code.fighters.entity.enums.UltimateSkill;

@SuppressWarnings("null")
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repositorios - Tests de Base de Datos")
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerRepository playerRepository;

    private User user1;
    private User user2;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
        userRepository.deleteAll();
        user1 = userRepository.save(User.builder()
                .username("fighter1")
                .email("fighter1@test.com")
                .password("pass")
                .build());
        user2 = userRepository.save(User.builder()
                .username("fighter2")
                .email("fighter2@test.com")
                .password("pass")
                .build());
        player1 = Player.builder().user(user1).build();
        player1.setWins(10);
        player1 = playerRepository.save(player1);
        player2 = Player.builder().user(user2).build();
        player2.setWins(5);
        player2 = playerRepository.save(player2);
    }

    @Nested
    @DisplayName("UserRepository")
    class UserRepositoryTests {

        @Test
        @DisplayName("findByEmail - encuentra usuario existente")
        void findByEmail_found() {
            Optional<User> result = userRepository.findByEmail("fighter1@test.com");
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("fighter1");
        }

        @Test
        @DisplayName("findByEmail - vacío si el email no existe")
        void findByEmail_notFound() {
            assertThat(userRepository.findByEmail("noexiste@test.com")).isEmpty();
        }

        @Test
        @DisplayName("findByUsername - encuentra usuario existente")
        void findByUsername_found() {
            Optional<User> result = userRepository.findByUsername("fighter1");
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("fighter1@test.com");
        }

        @Test
        @DisplayName("findByUsername - vacío si no existe")
        void findByUsername_notFound() {
            assertThat(userRepository.findByUsername("desconocido")).isEmpty();
        }

        @Test
        @DisplayName("existsByEmail - true si el email existe")
        void existsByEmail_true() {
            assertThat(userRepository.existsByEmail("fighter1@test.com")).isTrue();
        }

        @Test
        @DisplayName("existsByEmail - false si el email no existe")
        void existsByEmail_false() {
            assertThat(userRepository.existsByEmail("nuevo@test.com")).isFalse();
        }

        @Test
        @DisplayName("existsByUsername - true si el username existe")
        void existsByUsername_true() {
            assertThat(userRepository.existsByUsername("fighter1")).isTrue();
        }

        @Test
        @DisplayName("existsByUsername - false si el username no existe")
        void existsByUsername_false() {
            assertThat(userRepository.existsByUsername("desconocido")).isFalse();
        }

        @Test
        @DisplayName("save - persiste el usuario con createdAt automático")
        void save_persistsWithCreatedAt() {
            User saved = userRepository.findByEmail("fighter1@test.com").orElseThrow();
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("email único - no permite duplicados")
        void email_uniqueConstraintPreventsReplica() {
            User duplicate = User.builder()
                    .username("otro")
                    .email("fighter1@test.com")
                    .password("pass")
                    .build();
            assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate));
        }

        @Test
        @DisplayName("username único - no permite duplicados")
        void username_uniqueConstraintPreventsReplica() {
            User duplicate = User.builder()
                    .username("fighter1")
                    .email("otro@test.com")
                    .password("pass")
                    .build();
            assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate));
        }
    }

    @Nested
    @DisplayName("PlayerRepository")
    class PlayerRepositoryTests {

        @Test
        @DisplayName("findByUserId - encuentra el personaje asociado al usuario")
        void findByUserId_found() {
            Optional<Player> result = playerRepository.findByUserId(user1.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getUser().getEmail()).isEqualTo("fighter1@test.com");
        }

        @Test
        @DisplayName("findByUserId - vacío si el userId no existe")
        void findByUserId_notFound() {
            assertThat(playerRepository.findByUserId(999L)).isEmpty();
        }

        @Test
        @DisplayName("findAllExcept - devuelve todos los jugadores menos el indicado")
        void findAllExcept_excludesGivenId() {
            List<Player> result = playerRepository.findAllExcept(player1.getId());
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isNotEqualTo(player1.getId());
        }

        @Test
        @DisplayName("findAllExcept - devuelve lista vacía si solo hay un jugador")
        void findAllExcept_emptyWhenOnlyOnePlayer() {
            playerRepository.delete(player2);
            userRepository.delete(user2);
            assertThat(playerRepository.findAllExcept(player1.getId())).isEmpty();
        }

        @Test
        @DisplayName("findTop10ByOrderByWinsDesc - el primero tiene más victorias que el segundo")
        void findTop10ByOrderByWinsDesc_orderedDescending() {
            List<Player> ranking = playerRepository.findTop10ByOrderByWinsDesc();
            assertThat(ranking).hasSize(2);
            assertThat(ranking.get(0).getWins()).isGreaterThanOrEqualTo(ranking.get(1).getWins());
            assertThat(ranking.get(0).getWins()).isEqualTo(10);
        }

        @Test
        @DisplayName("findTop10ByOrderByWinsDesc - devuelve máximo 10 jugadores aunque haya más")
        void findTop10ByOrderByWinsDesc_limitsTo10() {
            for (int i = 3; i <= 15; i++) {
                User u = userRepository.save(User.builder()
                        .username("fighter" + i)
                        .email("fighter" + i + "@test.com")
                        .password("pass")
                        .build());
                Player p = Player.builder().user(u).build();
                p.setWins(i);
                playerRepository.save(p);
            }
            List<Player> ranking = playerRepository.findTop10ByOrderByWinsDesc();
            assertThat(ranking).hasSizeLessThanOrEqualTo(10);
        }

        @Test
        @DisplayName("save - los valores por defecto del Player se persisten correctamente")
        void save_persistsDefaultValues() {
            User newUser = userRepository.save(User.builder()
                    .username("nuevo")
                    .email("nuevo@test.com")
                    .password("pass")
                    .build());
            Player newPlayer = playerRepository.save(Player.builder().user(newUser).build());
            assertThat(newPlayer.getId()).isNotNull();
            assertThat(newPlayer.getHealth()).isEqualTo(100);
            assertThat(newPlayer.getWins()).isEqualTo(0);
            assertThat(newPlayer.getLosses()).isEqualTo(0);
            assertThat(newPlayer.getSkinColor()).isEqualTo("#f5c5a3");
            assertThat(newPlayer.getHairStyle()).isEqualTo("ponytail");
            assertThat(newPlayer.getHairColor()).isEqualTo("#7c3aed");
            assertThat(newPlayer.getEyeColor()).isEqualTo("#2563eb");
            assertThat(newPlayer.getOutfit()).isEqualTo("hoodie");
            assertThat(newPlayer.getOutfitColor()).isEqualTo("#1e1b4b");
            assertThat(newPlayer.getAccessory()).isEqualTo("none");
            assertThat(newPlayer.getUltimateSkill()).isEqualTo(UltimateSkill.FRIDAY_DEPLOY);
        }

        @Test
        @DisplayName("relación OneToOne User-Player - un usuario solo puede tener un personaje")
        void oneToOne_userCanHaveOnlyOnePlayer() {
            Player duplicate = Player.builder().user(user1).build();
            assertThatThrownBy(() -> playerRepository.saveAndFlush(duplicate));
        }
    }
}
