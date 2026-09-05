package org.example.dao;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.DBException;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * DAO для выполнения CRUD операций с сущностью {@link User}.
 *
 * Использует Hibernate для взаимодействия с базой данных и управляет транзакциями при выполнении операций
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserDao implements Dao<User> {
    @Getter
    private static UserDao instance = new UserDao();

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param object пользователь для сохранения
     * @return сохранённый пользователь
     * @throws DBException если произошла ошибка при сохранении
     */
    @Override
    public User save(User object) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(object);
                transaction.commit();
                log.info("User saved successfully: {}", object);
                return object;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Failed to save user: {}", object);
                throw new DBException("Failed to save user");
            }
        }
    }

    /**
     * Находит пользователя в базе данных по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return {@link Optional} с найденным пользователем или пустой {@link Optional},
     * если пользователь не найден
     * @throws DBException если произошла ошибка при поиске
     */
    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Optional<User> user = Optional.ofNullable(session.get(User.class, id));
                transaction.commit();
                log.info("User with id {} is {}", id, user.isPresent() ? "found" : "not found");
                return user;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Failed to find user with id: {}", id);
                throw new DBException("Failed to find user with id: " + id);
            }
        }
   }

    /**
     * Извлекает список всех пользователей из базы данных.
     *
     * @return список всех пользователей
     * @throws DBException если произошла ошибка при получении пользователей
     */
    @Override
   public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                List<User> users = session.createQuery("FROM User", User.class).list();
                transaction.commit();
                log.info("Found {} users", users.size());
                return users;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Failed to find all users");
                throw new DBException("Failed to find all users");
            }
        }
   }
    /**
     * Обновляет существующего пользователя в базе данных.
     *
     * @param object пользователь с обновлёнными данными
     * @return обновлённый пользователь
     * @throws DBException если произошла ошибка при обновлении
     */
    @Override
    public User update(User object) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User updatedUser = session.merge(object);
                transaction.commit();
                log.info("User updated successfully: {}", updatedUser);
                return updatedUser;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Failed to update user: {}", object);
                throw new DBException("Failed to update user");
            }
        }
    }
    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws DBException если произошла ошибка при удалении
     */
    @Override
    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user == null) {
                    log.warn("User not found for deletion with id: {}", id);
                    transaction.rollback();
                    throw new UserNotFoundException(id);
                }

                session.remove(user);
                transaction.commit();
                log.info("User deleted successfully with id: {}", id);
            } catch (UserNotFoundException e) {
                throw e;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Failed to delete user with id: {}", id);
                throw new DBException("Failed to delete user with id: " + id);
            }
        }
    }
}
