package org.gateway.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@TestPropertySource(properties = {
        "resilience4j.circuitbreaker.instances.userService.sliding-window-size=2",
        "resilience4j.circuitbreaker.instances.userService.minimum-number-of-calls=2",
        "resilience4j.circuitbreaker.instances.userService.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.userService.wait-duration-in-open-state=2s",
        "resilience4j.circuitbreaker.instances.userService.permitted-number-of-calls-in-half-open-state=1",
        "resilience4j.timelimiter.instances.userService.timeout-duration=5s"
})
class CircuitBreakerRecoveryE2ETest extends AbstractGatewayE2ETest {

    @Test
    void shouldRecoverThroughHalfOpenStateAfterBackendRestored() throws InterruptedException {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(serverError()));

        restTestClient.get().uri("/api/users/1").exchange().expectStatus().isEqualTo(503);
        restTestClient.get().uri("/api/users/1").exchange().expectStatus().isEqualTo(503);

        USER_SERVICE_MOCK.resetAll();
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(okJson("""
                        {"id":1,"name":"John Doe","email":"john@example.com","age":30}
                        """)));

        Thread.sleep(3000);

        restTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe");

        restTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }
}
