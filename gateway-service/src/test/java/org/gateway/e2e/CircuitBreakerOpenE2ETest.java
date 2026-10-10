package org.gateway.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@TestPropertySource(properties = {
        "resilience4j.circuitbreaker.instances.userService.sliding-window-size=2",
        "resilience4j.circuitbreaker.instances.userService.minimum-number-of-calls=2",
        "resilience4j.circuitbreaker.instances.userService.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.userService.wait-duration-in-open-state=30s",
        "resilience4j.timelimiter.instances.userService.timeout-duration=5s"
})
class CircuitBreakerOpenE2ETest extends AbstractGatewayE2ETest {

    @Test
    void shouldOpenCircuitAndReturnFallbackWithoutCallingBackend() {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(serverError()));

        for (int i = 0; i < 2; i++) {
            restTestClient.get().uri("/api/users/1")
                    .exchange()
                    .expectStatus().isEqualTo(503)
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(503)
                    .jsonPath("$.message").isEqualTo("Service is temporarily unavailable. Please try again later.")
                    .jsonPath("$.timestamp").exists();
        }

        restTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isEqualTo(503);

        USER_SERVICE_MOCK.verify(2, getRequestedFor(urlEqualTo("/api/users/1")));
    }
}
