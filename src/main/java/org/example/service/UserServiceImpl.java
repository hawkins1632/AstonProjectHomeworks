package org.example.service;
import org.example.dao.Dao;
import org.example.dao.UserDao;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import java.util.List;

/**
 * Реализации сервиса пользователей.
 * Управляет бизнес-правилами, валидацией сущностей перед отправкой в слой DAO
 * и обработкой исключений. Реализован как Singleton.
 */
public class UserServiceImpl implements UserService{
    private static final UserServiceImpl INSTANCE = new UserServiceImpl(org.example.dao.UserDao.getInstance());

    private final Dao<User> userDao;

    /**
     * Конструктор скрыт, чтобы никто не мог создать дубликат сервиса через "new".
     * Принимает интерфейс Dao через параметры (Dependency Injection), что необходимо
     * для подстановки моков в Mockito тестах.
     * @param userDao объект доступа к данным пользователей.
     */
    private UserServiceImpl(Dao<User>userDao){
        this.userDao = userDao;
    }

    /**
     * Публичный метод для получения доступа к единственному экземпляру сервиса.
     * @return экземпляр {@link UserService}
     */
    public static UserService getInstance(){
        return INSTANCE;
    }

    @Override
    public User createUser(String name, String email, Integer age){
        validateUserData(name,email,age);
        User user = new User(name,email,age);
        return userDao.save(user);
    }

    @Override
    public User getUserById(Long id){
        if (id == null){
            throw new IllegalArgumentException("Идентификатор пользователя (ID) не может быть null");
        }
        return userDao.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String  email, Integer age){
        User existingUser = getUserById(id);

        validateUserData(name,email,age);

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);

        return userDao.update(existingUser);

    }

    @Override
    public void deleteUser(Long id){
        getUserById(id);

        userDao.delete(id);
    }

    /**
     * Внутренний приватный метод для централизованной валидации бизнес правил пользователя
     * @throws IllegalArgumentException если данные некорректны
     */
    private void validateUserData(String name, String email, Integer age){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (email == null || email.isBlank() || !email.contains("@") || !email.contains(".")){
            throw new IllegalArgumentException("Некорректный формат email. Он должен содержать '@'  и '.'");
        }
        if (age == null || age <0){
            throw new IllegalArgumentException("Возраст пользователя не может быть отрицательным");
        }
    }
}
