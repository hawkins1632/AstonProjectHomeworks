package org.example.service;
import org.example.model.User;
import java.util.List;

/**
 * Сервис для управления бизнес-логикой работы с пользователями.
 * Определяет контракт для валидации данных, обработки исключений и координирует
 * взаимодействие между консольным интерфейсом и слоем доступа к данным (DAO).
 */
public interface UserService {

    /**
     * Создаёт и сохраняет нового пользователя в системе после прохождения валидации
     * @param name имя пользователя
     * @param email электронная почта пользователя
     * @param age возраст пользователя
     * @return сохранённый объект {@link User} с присвоенным идентификатором
     * @throws IllegalArgumentException если входные данные не прошли валидацию
     */
    User createUser(String name, String email, Integer age);

    /**
     * Возвращает пользователя по его идентификатору
     * @param id уникальный идентификатор пользователя
     * @return найденный объект {@link User}
     * @throws org.example.exception.UserNotFoundException если пользователь не найден
     */
    User getUserById(Long id);

    /**
     * Извлекает список всех созданных пользователей
     * @return список пользователей
     */
    List<User> getAllUsers();

    /**
     * Обновляет данные уже существующего пользователя
     * @param id идентификатор обновляемого пользователя
     * @param name новое имя
     * @param email новый email
     * @param age новый возраст
     * @return обновлённый объект {@link User}
     * @throws org.example.exception.UserNotFoundException если пользователь не найден
     * @throws IllegalArgumentException если новые данные некорректны
     */
    User updateUser(Long id, String name, String email, Integer age);

    /**
     * Удаляет пользователя из системы по его идентификатор
     * @param id уникальный идентификатор пользователя
     * @throws org.example.exception.UserNotFoundException если пользователь не найден
     */
    void deleteUser(Long id);
}
