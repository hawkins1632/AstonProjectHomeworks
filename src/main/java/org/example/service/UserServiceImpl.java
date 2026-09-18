package org.example.service;

import org.example.model.User;

import java.util.List;

/**
 * Реализация интерфейса {@link UserService} для управления бизнес-логикой пользователей.
 * Отвечает за валидацию идентификаторов и персональных данных, обработку бизнес-исключений
 * и прямое взаимодействие со слоем доступа к данным через.
 */
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
