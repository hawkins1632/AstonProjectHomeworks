package org.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("REST API микросервиса user-service для управления пользователями.\n"
                                + "\n"
                                + "Предоставляет операции CRUD:\n"
                                + "- создание пользователя (POST /api/users);\n"
                                + "- получение пользователя по идентификатору (GET /api/users/{id});\n"
                                + "- получение списка всех пользователей (GET /api/users);\n"
                                + "- обновление пользователя (PUT /api/users/{id});\n"
                                + "- удаление пользователя (DELETE /api/users/{id}).\n"
                                + "\n"
                                + "Ошибки возвращаются в едином формате JSON:\n"
                                + "- {timestamp, status, message} — стандартная ошибка (400/404);\n"
                                + "- {timestamp, status, errors} — ошибка валидации полей запроса (400).\n")
                        .version("1.0.0")
                        .contact(new Contact().name("Team"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Локальный сервер разработки")));
    }
}