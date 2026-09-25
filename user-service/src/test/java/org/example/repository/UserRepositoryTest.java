package org.example.repository;

import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17")
            .withDatabaseName("testdb")
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

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Alice", saved.getName());
        assertEquals("alice@mail.com", saved.getEmail());
        assertEquals(30, saved.getAge());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.save(new User("Alice", "alice@mail.com", 30));
        userRepository.save(new User("Bob", "bob@mail.com", 25));

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findById_shouldReturnUser() {
        User user = userRepository.save(new User("Bob", "bob@mail.com", 25));

        Optional<User> found = userRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    void update_shouldUpdateExistingUser() {
        User user = userRepository.save(new User("Alice", "alice@mail.com", 30));

        user.setName("Alicia");
        user.setEmail("alicia@mail.com");
        userRepository.save(user);

        Optional<User> updated = userRepository.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Alicia", updated.get().getName());
        assertEquals("alicia@mail.com", updated.get().getEmail());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User user = userRepository.save(new User("Charlie", "charlie@mail.com", 35));

        userRepository.deleteById(user.getId());

        assertFalse(userRepository.existsUserById(user.getId()));
    }

    @Test
    void existsUserById_shouldReturnFalseForNonExistentId() {
        assertFalse(userRepository.existsUserById(999L));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        userRepository.save(new User("Alice", "alice@mail.com", 30));

        assertThrows(Exception.class, () ->
                userRepository.save(new User("Bob", "alice@mail.com", 25)));
    }
}
