package org.example.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.dao.UserDao;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Реализация интерфейса {@link UserService} для управления бизнес-логикой пользователей.
 * Отвечает за валидацию идентификаторов и персональных данных, обработку бизнес-исключений
 * и прямое взаимодействие со слоем доступа к данным через {@link UserDao}.
 */
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Getter
    private static final UserServiceImpl INSTANCE = new UserServiceImpl(UserDao.getInstance());

    @Override
    public User createUser(User user) {
        validateUserData(user);
        return userDao.save(user);
    }

    @Override
    public User getUserById(Long id) {
        validateId(id);
        return userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(User updatedData) {
        validateUserData(updatedData);
        validateId(updatedData.getId());

        return userDao.update(updatedData);
    }

    @Override
    public void deleteUser(Long id) {
        validateId(id);

        userDao.delete(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }

    private void validateUserData(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User object cannot be null");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getAge() == null || user.getAge() < 0) {
            throw new IllegalArgumentException("User age cannot be negative");
        }
    }
}
