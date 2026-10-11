import org.example.configserver.ConfigServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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
    }

    @Test
    void configClientShouldLoadRemoteProperty() {
        PropertySource<?> remoteProperties = loadRemoteProperties();

        assertThat(remoteProperties).isNotNull();
        assertThat(remoteProperties.getProperty("app.test-property"))
                .isEqualTo("from-config-server");
    }

    @Test
    void localPropertyShouldOverrideRemoteProperty() {
        PropertySource<?> remoteProperties = loadRemoteProperties();

        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(remoteProperties);
        environment.getPropertySources().addFirst(
                new MapPropertySource(
                        "local-test",
                        Map.of("app.test-property", "local-override")
                )
        );

        assertThat(environment.getProperty("app.test-property"))
                .isEqualTo("local-override");
    }

    private PropertySource<?> loadRemoteProperties() {
        StandardEnvironment environment = new StandardEnvironment();

        environment.getPropertySources().addFirst(
                new MapPropertySource(
                        "test-application",
                        Map.of("spring.application.name", "user-service")
                )
        );

        ConfigClientProperties properties =
                new ConfigClientProperties(environment);

        properties.setName("user-service");
        properties.setProfile("default");
        properties.setUri(new String[]{"http://localhost:" + port});
        properties.setFailFast(true);

        return new ConfigServicePropertySourceLocator(properties)
                .locate(environment);
    }
}