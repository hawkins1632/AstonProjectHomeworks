import org.example.configserver.ConfigServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ConfigServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.server.native.search-locations=classpath:/test-config",
                "spring.profiles.active=native"
        }
)
class ConfigServerApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertThat(port).isPositive();
    }

    @Test
    void shouldReturnClientConfiguration() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port + "/user-service/default"
                ))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("from-config-server");
        assertThat(response.body()).contains("app.test-property");
    }

    @Test
    void configClientShouldLoadRemoteProperty() {
        try (ConfigurableApplicationContext client = startClient()) {
            assertThat(client.getEnvironment()
                    .getProperty("app.test-property"))
                    .isEqualTo("from-config-server");
        }
    }

    @Test
    void localPropertyShouldOverrideRemoteProperty() {
        try (ConfigurableApplicationContext client = startClient(
                "--app.test-property=local-override"
        )) {
            assertThat(client.getEnvironment()
                    .getProperty("app.test-property"))
                    .isEqualTo("local-override");
        }
    }

    private ConfigurableApplicationContext startClient(String... extraArgs) {

        String[] args = new String[5 + extraArgs.length];

        args[0] = "--spring.application.name=user-service";
        args[1] = "--spring.config.import=configserver:http://localhost:" + port;
        args[2] = "--spring.cloud.config.fail-fast=true";
        args[3] = "--spring.main.web-application-type=none";
        args[4] = "--spring.cloud.config.enabled=true";

        System.arraycopy(extraArgs, 0, args, 5, extraArgs.length);

        return new SpringApplicationBuilder(TestClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @SpringBootConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestClientApplication {
    }
}