package org.service.repository;

import org.service.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User user = new User("Alice", "alice@mail.com", 30);

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("alice@mail.com");
        assertThat(saved.getAge()).isEqualTo(30);
    }

    @Test
    void save_shouldThrow_whenEmailAlreadyExists() {
        userRepository.saveAndFlush(new User("Alice", "alice@mail.com", 30));

        assertThatThrownBy(() ->
                userRepository.saveAndFlush(new User("Bob", "alice@mail.com", 25))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.saveAndFlush(new User("Alice", "alice@mail.com", 30));
        userRepository.saveAndFlush(new User("Bob", "bob@mail.com", 25));

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("alice@mail.com", "bob@mail.com");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsers() {
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void findById_shouldReturnUser() {
        User user = userRepository.saveAndFlush(new User("Bob", "bob@mail.com", 25));

        Optional<User> found = userRepository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bob");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        assertThat(userRepository.findById(999L)).isEmpty();
    }

    @Test
    void update_shouldUpdateExistingUser() {
        User user = userRepository.save(new User("Alice", "alice@mail.com", 30));

        user.setName("Alicia");
        user.setEmail("alicia@mail.com");
        userRepository.saveAndFlush(user);

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Alicia");
        assertThat(updated.getEmail()).isEqualTo("alicia@mail.com");
        assertThat(updated.getAge()).isEqualTo(30);
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User user = userRepository.save(new User("Charlie", "charlie@mail.com", 35));

        userRepository.deleteById(user.getId());
        userRepository.flush();

        assertThat(userRepository.existsUserById(user.getId())).isFalse();
    }

    @Test
    void existsUserById_shouldReturnTrue_whenExists() {
        User user = userRepository.saveAndFlush(new User("Alice", "alice@mail.com", 30));

        assertThat(userRepository.existsUserById(user.getId())).isTrue();
    }

    @Test
    void existsUserById_shouldReturnFalseForNonExistentId() {
        assertThat(userRepository.existsUserById(999L)).isFalse();
    }
}
