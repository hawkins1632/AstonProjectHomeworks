package org.service.e2e;

import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.Test;
import org.service.dto.UserRequestDto;
import org.service.dto.UserResponseDto;
import org.service.model.User;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserKafkaE2ETest extends AbstractE2ETest {

    @Test
    void createUser_shouldPublishCreatedEvent() {
        UserRequestDto request = new UserRequestDto("Alice", "alice@e2e.com", 30);

        UserResponseDto response = restTestClient.post()
                .uri("/api/users")
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();

        UserEvent event = awaitEvent("alice@e2e.com", UserEventType.CREATED);

        assertThat(event.id()).isEqualTo(response.getId());
        assertThat(event.email()).isEqualTo("alice@e2e.com");
        assertThat(event.type()).isEqualTo(UserEventType.CREATED);
    }

    @Test
    void updateUser_shouldPublishUpdatedEvent() {
        User saved = userRepository.save(new User("Bob", "bob@e2e.com", 25));

        UserRequestDto request = new UserRequestDto("Bobby", "bobby@e2e.com", 26);
        restTestClient.put()
                .uri("/api/users/{id}", saved.getId())
                .body(request)
                .exchange()
                .expectStatus().isOk();

        UserEvent event = awaitEvent("bobby@e2e.com", UserEventType.UPDATED);

        assertThat(event.id()).isEqualTo(saved.getId());
        assertThat(event.email()).isEqualTo("bobby@e2e.com");
    }

    @Test
    void deleteUser_shouldPublishDeletedEvent() {
        User saved = userRepository.save(new User("Charlie", "charlie@e2e.com", 35));

        restTestClient.delete()
                .uri("/api/users/{id}", saved.getId())
                .exchange()
                .expectStatus().isNoContent();

        UserEvent event = awaitEvent("charlie@e2e.com", UserEventType.DELETED);

        assertThat(event.id()).isEqualTo(saved.getId());
        assertThat(event.email()).isEqualTo("charlie@e2e.com");
    }
}