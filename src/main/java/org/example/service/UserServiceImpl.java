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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Getter
    private static final UserServiceImpl INSTANCE = new UserServiceImpl();

    @Override
    public User createUser(User user) {
        validateUserData(user);
        return UserDao.getInstance().save(user);
    }

    @Override
    public User getUserById(long id) {
        validateId(id);
        return UserDao.getInstance().findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers() {
        return UserDao.getInstance().findAll();
    }

    @Override
    public User updateUser(User updatedData) {
        validateUserData(updatedData);

        return UserDao.getInstance().update(updatedData);
    }

    @Override
    public void deleteUser(long id) {
        validateId(id);

        UserDao.getInstance().findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        UserDao.getInstance().delete(id);
    }

    private void validateId(long id) {
        if (id <= 0) {
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
        if (user.getAge() < 0) {
            throw new IllegalArgumentException("User age cannot be negative");
        }
    }
}
