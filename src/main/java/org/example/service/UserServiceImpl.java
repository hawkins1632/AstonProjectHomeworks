package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;

import java.util.List;

/**
 * Реализация интерфейса {@link UserService} для управления бизнес-логикой пользователей.
 * Отвечает за валидацию идентификаторов и персональных данных, обработку бизнес-исключений
 * и прямое взаимодействие со слоем доступа к данным через.
 */
public class UserServiceImpl implements UserService {

    @Override
    public UserResponseDto create(UserRequestDto requestDto) {
        return null;
    }

    @Override
    public UserResponseDto getById(Long id) {
        return null;
    }

    @Override
    public List<UserResponseDto> getAll() {
        return List.of();
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
