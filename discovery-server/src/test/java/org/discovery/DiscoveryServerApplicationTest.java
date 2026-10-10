package org.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class DiscoveryServerApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void eurekaEndpointIsAvailable() {
        restTestClient.get()
                .uri("http://localhost:" + port + "/eureka/apps")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void healthEndpointIsUp() {
        String response = restTestClient.get()
                .uri("http://localhost:" + port + "/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).contains("\"status\":\"UP\"");
    }

}