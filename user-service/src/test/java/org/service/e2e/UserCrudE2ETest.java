package org.service.e2e;

import org.junit.jupiter.api.Test;
import org.service.dto.UserRequestDto;
import org.service.dto.UserResponseDto;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserCrudE2ETest extends AbstractE2ETest {
    @Test
    void fullLifecycleHttpTest() {
        UserRequestDto create = new UserRequestDto("Alice", "alice@e2e.com", 30);

        UserResponseDto created = restTestClient.post()
                .uri("/api/users")
                .body(create)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(created).isNotNull();
        Long id = created.getId();
        assertThat(id).isNotNull();

        UserResponseDto fetched = restTestClient.get()
                .uri("/api/users/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo("Alice");
        assertThat(fetched.getEmail()).isEqualTo("alice@e2e.com");
        assertThat(fetched.getAge()).isEqualTo(30);

        UserRequestDto update = new UserRequestDto("Alicia", "alicia@e2e.com", 31);
        restTestClient.put()
                .uri("/api/users/{id}", id)
                .body(update)
                .exchange()
                .expectStatus().isOk();

        UserResponseDto updated = restTestClient.get()
                .uri("/api/users/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Alicia");
        assertThat(updated.getEmail()).isEqualTo("alicia@e2e.com");
        assertThat(updated.getAge()).isEqualTo(31);

        restTestClient.delete()
                .uri("/api/users/{id}", id)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    void getAll_shouldReturnPersistedUsers() {
        restTestClient.post().uri("/api/users")
                .body(new UserRequestDto("Alice", "alice@e2e.com", 30))
                .exchange().expectStatus().isCreated();

        restTestClient.post().uri("/api/users")
                .body(new UserRequestDto("Bob", "bob@e2e.com", 25))
                .exchange().expectStatus().isCreated();

        UserResponseDto[] users = restTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto[].class)
                .returnResult().getResponseBody();

        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(UserResponseDto::getEmail)
                .containsExactlyInAnyOrder("alice@e2e.com", "bob@e2e.com");
    }
}