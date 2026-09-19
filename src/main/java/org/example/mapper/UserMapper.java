package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
/**
 * Маппер для преобразования между сущностью {@link User} и DTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Преобразует сущность пользователя в DTO для ответа.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    UserResponseDto toResponse(User user);
    /**
     * Преобразует список сущностей пользователей в список DTO.
     *
     * @param userList список сущностей пользователей
     * @return список DTO пользователей
     */
    List<UserResponseDto> toResponseList(List<User> userList);
    /**
     * Обновляет существующую сущность пользователя данными из запроса.
     *
     * @param user сущность, которую нужно обновить
     * @param request DTO с новыми данными
     */
    void updateEntity(@MappingTarget User user, UserRequestDto request);
    /**
     * Преобразует DTO запроса в сущность пользователя.
     *
     * @param request DTO с данными для создания пользователя
     * @return сущность пользователя
     */
    User toEntity(UserRequestDto request);
}
