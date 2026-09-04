package org.example.dao;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDao implements Dao<User>{

    @Override
    public User save(User object) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(object);
            transaction.commit();
            log.info("User saved successfully: {}", object);
            return object;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to save user: {}", object, e);
            //TODO: create own Exception
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Находит пользователя в БД по его уникальному ID
     *
     * @param id уникальный идентификатор пользователя в системе
     * @return объект найденного пользователя User, либо null если запись отсутствует
     */
    public Optional<User> findById(Long id){
        // Открываем сессию взаимодействия с БД через утильный класс
       try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           // Для чтения транзакции не требуются, используем встроенный метод get
           return Optional.ofNullable(session.get(User.class, id));
       }
   }

    /**
     * Извлекает полный список всех зарегистрированных пользователей из базы данных.
     * Использует язык запросов HQL
     *
     * @return список (List) всех существующих объектов User в базе данных
     */
   public List<User> findAll(){
       // Открываем автоматическую сессию в блоке try-with-resources
       try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           // Создаём HQL запрос "from User", извлекающий все записи из таблицы
           return session.createQuery("from User", User.class).list();

       }
   }

    @Override
    public User update(User object) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(object);
            transaction.commit();
            log.info("User updated successfully: {}", updatedUser);
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to update user: {}", object, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                log.info("User deleted successfully with id: {}", id);
            } else {
                transaction.rollback();
                log.warn("User not found for deletion with id: {}", id);
                throw new RuntimeException("User not found with id: " + id);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to delete user with id: {}", id, e);
            throw new RuntimeException("Failed to delete user with id: " + id, e);
        }
    }
}
