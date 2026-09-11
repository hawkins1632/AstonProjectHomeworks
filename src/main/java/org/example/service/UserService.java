package org.example.service;

import org.example.model.User;
import java.util.List;

/**
 * Сервис для управления бизнес-логикой работы с пользователями.
 * Координирует валидацию данных, обработку исключений и взаимодействие со слоем доступа к данным.
 */
public interface UserService {

    /**
     * Создает нового пользователя в системе после предварительной валидации его данных.
     *
     * @param user объект пользователя, содержащий данные для регистрации
     * @return сохраненный объект пользователя с присвоенным уникальным идентификатором
     * @throws IllegalArgumentException если переданный объект или его поля не прошли валидацию
     */
    User createUser(User user);

    /**
     * Возвращает пользователя по его уникальному числовому идентификатору.
     *
     * @param id уникальный числовой идентификатор пользователя
     * @return найденный объект пользователя
     * @throws IllegalArgumentException если переданный id меньше или равен нулю
     * @throws org.example.exception.UserNotFoundException если пользователь отсутствует в базе данных
     */
    User getUserById(Long id);

    /**
     * Возвращает список всех зарегистрированных пользователей системы.
     *
     * @return список объектов пользователей, либо пустой список, если пользователи отсутствуют
     */
    List<User> getAllUsers();

    /**
     * Обновляет персональные данные существующего пользователя.
     *
     * @param user объект с новыми персональными данными для обновления
     * @return обновленный объект пользователя, сохраненный в базе данных
     * @throws IllegalArgumentException если id или новые данные не прошли проверку валидности
     */
    User updateUser(User user);

    /**
     * Удаляет пользователя из системы по его идентификатору.
     *
     * @param id unique numerical user identifier
     * @throws IllegalArgumentException если переданный id меньше или равен нулю
     * @throws org.example.exception.UserNotFoundException если удаляемый пользователь отсутствует в системе
     */
    void deleteUser(Long id);
}
