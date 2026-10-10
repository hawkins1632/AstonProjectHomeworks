package org.gateway.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractGatewayE2ETest {

    protected static final WireMockServer USER_SERVICE_MOCK = new WireMockServer(options().dynamicPort());
    protected static final WireMockServer NOTIFICATION_SERVICE_MOCK = new WireMockServer(options().dynamicPort());

    @BeforeAll
    static void startMocks() {
        if (!USER_SERVICE_MOCK.isRunning()) USER_SERVICE_MOCK.start();
        if (!NOTIFICATION_SERVICE_MOCK.isRunning()) NOTIFICATION_SERVICE_MOCK.start();
    }

    @AfterAll
    static void stopMocks() {
        USER_SERVICE_MOCK.stop();
        NOTIFICATION_SERVICE_MOCK.stop();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("USER_SERVICE_URI", () -> "http://127.0.0.1:" + USER_SERVICE_MOCK.port());
        registry.add("NOTIFICATION_SERVICE_URI", () -> "http://127.0.0.1:" + NOTIFICATION_SERVICE_MOCK.port());
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
        registry.add("spring.cloud.gateway.server.webflux.discovery.locator.enabled", () -> "false");
    }

    @LocalServerPort
    protected int port;

    protected RestTestClient restTestClient;

    @BeforeEach
    void setUpClientAndMocks() {
        restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        USER_SERVICE_MOCK.resetAll();
        NOTIFICATION_SERVICE_MOCK.resetAll();
    }
}
