package org.service.controller;

import org.junit.jupiter.api.Test;
import org.service.service.UserService;
import org.service.config.OpenApiConfig;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет, что OpenAPI/Swagger документация генерируется корректно:
 * {@code /v3/api-docs} доступен и полно описывает все endpoint'ы,
 * а Swagger UI отдаётся по {@code /swagger-ui/index.html}.
 */
@WebMvcTest(UserController.class)
@Import(OpenApiConfig.class)
@ImportAutoConfiguration(classes = {
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class,
        SpringDocWebMvcConfiguration.class,
        SwaggerConfig.class
})
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void openApiDocs_shouldDescribeAllEndpointsWithAllResponses() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("User Service API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.servers[0].url").value("http://localhost:8080"))
                .andExpect(jsonPath("$.components.schemas.UserRequestDto.properties.name.maxLength").value(100))
                .andExpect(jsonPath("$.components.schemas.UserResponseDto.properties.id.type").value("integer"))
                .andExpect(jsonPath("$.components.schemas.UserResponseDto.properties.id.format").value("int64"))
                .andExpect(jsonPath("$.components.schemas.UserResponseDto.properties.age.type").value("integer"))
                .andExpect(jsonPath("$.components.schemas.UserRequestDto.properties.age.type").value("integer"))
                .andExpect(jsonPath("$.paths['/api/users'].post.responses['201'].description").value("Пользователь успешно создан"))
                .andExpect(jsonPath("$.paths['/api/users'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/users'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].get.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].put.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].put.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].put.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].put.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].delete.responses['204']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].delete.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}'].delete.responses['404']").exists())
                .andExpect(jsonPath("$.components.schemas.ErrorResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ValidationErrorResponse").exists())
                .andExpect(jsonPath("$.components.schemas.UserRequestDto").exists())
                .andExpect(jsonPath("$.components.schemas.UserResponseDto").exists());
    }

    @Test
    void swaggerUi_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}