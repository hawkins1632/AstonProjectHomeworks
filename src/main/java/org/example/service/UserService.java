package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;

import java.util.List;

/**
 * Сервис для управления бизнес-логикой работы с пользователями.
 * Координирует валидацию данных, обработку исключений и взаимодействие со слоем доступа к данным.
 */
public interface UserService {

    /**
     * Создаёт нового пользователя в системе после предварительной валидации его данных.
     *
     * @param requestDto объект с данными пользователя для создания
     * @return сохранённый пользователь с присвоенным уникальным идентификатором
     */
    UserResponseDto create(UserRequestDto requestDto);

    /**
     * Возвращает пользователя по его уникальному числовому идентификатору.
     *
     * @param id уникальный числовой идентификатор пользователя
     * @return найденный пользователь
     * @throws org.example.exception.UserNotFoundException если пользователь отсутствует в базе данных
     */
    UserResponseDto getById(Long id);

    /**
     * Возвращает список всех зарегистрированных пользователей системы.
     *
     * @return список пользователей либо пустой список, если пользователи отсутствуют
     */
    List<UserResponseDto> getAll();

    /**
     * Обновляет персональные данные существующего пользователя.
     *
     * @param id         уникальный идентификатор пользователя
     * @param requestDto объект с новыми данными пользователя
     * @return обновлённый пользователь, сохранённый в базе данных
     * @throws org.example.exception.UserNotFoundException если пользователь с указанным id не найден
     */
    UserResponseDto update(Long id, UserRequestDto requestDto);

    /**
     * Удаляет пользователя из системы по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @throws org.example.exception.UserNotFoundException если удаляемый пользователь отсутствует в системе
     */
    void delete(Long id);
}
