package org.gateway.e2e;

import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

class GatewayRoutingE2ETest extends AbstractGatewayE2ETest {

    @Test
    void shouldRouteGetUserToUserService() {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(okJson("""
                        {"id":1,"name":"John Doe","email":"john@example.com","age":30}
                        """)));

        restTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john@example.com");
    }

    @Test
    void shouldPassThroughNotFoundErrorFromUserService() {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"timestamp":"2026-10-10T12:00:00","status":404,"message":"Failed to find user with id: 999"}
                                """)));

        restTestClient.get().uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Failed to find user with id: 999");
    }

    @Test
    void shouldRoutePostCreateUserToUserService() {
        USER_SERVICE_MOCK.stubFor(post(urlEqualTo("/api/users"))
                .willReturn(created()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":5,"name":"Jane","email":"jane@example.com","age":25}
                                """)));

        restTestClient.post().uri("/api/users")
                .header("Content-Type", "application/json")
                .body("""
                        {"name":"Jane","email":"jane@example.com","age":25}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(5);
    }

    @Test
    void shouldRouteNotificationPathWithRewrite() {
        NOTIFICATION_SERVICE_MOCK.stubFor(post(urlEqualTo("/api/notification/email"))
                .willReturn(aResponse().withStatus(202)));

        restTestClient.post().uri("/api/notifications/email")
                .header("Content-Type", "application/json")
                .body("""
                        {"email":"john@example.com","type":"CREATED"}
                        """)
                .exchange()
                .expectStatus().isAccepted();

        NOTIFICATION_SERVICE_MOCK.verify(postRequestedFor(urlEqualTo("/api/notification/email")));
    }

    @Test
    void shouldReturnServerErrorAsFailureAndTriggerFallback() {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(serverError()));

        restTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.message").isEqualTo("Service is temporarily unavailable. Please try again later.")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldProxyUserApiDocs() {
        USER_SERVICE_MOCK.stubFor(get(urlEqualTo("/v3/api-docs"))
                .willReturn(okJson("""
                        {"openapi":"3.1.0","info":{"title":"user-service","version":"1.0"},"paths":{}}
                        """)));

        restTestClient.get().uri("/v3/api-docs/user-service")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.info.title").isEqualTo("user-service");
    }
}
