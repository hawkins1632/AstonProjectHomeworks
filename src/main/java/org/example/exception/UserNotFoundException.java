package org.example.exception;
/**
 * Исключение, выбрасываемое в случае, если запрашиваемый пользователь
 * не был найден в базе данных PostgreSQL.
 * Наследуется от {@link RuntimeException}, что позволяет использовать его
 * как необрабатываемое исключение в методах DAO и консольного интерфейса.
 */
public class UserNotFoundException extends RuntimeException{
    /**
     * Создает новое исключение с детализированным сообщением,
     * содержащим идентификатор ненайденного пользователя.
     * @param id уникальный идентификатор пользователя, которого не удалось найти
     */
    public UserNotFoundException(Long id) {
        super(String.format("Failed to find user with id: %d", id));
    }
}
