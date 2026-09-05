package org.example.dao;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserDao implements Dao<User>{

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param object пользователь для сохранения
     * @return сохранённый пользователь
     * @throws RuntimeException если произошла ошибка при сохранении
     */
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
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Находит пользователя в базе данных по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return {@link Optional} с найденным пользователем или пустой {@link Optional},
     * если пользователь не найден
     * @throws RuntimeException если произошла ошибка при поиске
     */
    @Override
    public Optional<User> findById(Long id){
       Transaction transaction = null;

       try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           transaction = session.beginTransaction();

           Optional<User>user = Optional.ofNullable(session.get(User.class, id));

           transaction.commit();
           log.info("Запрос findById для ID {}: {}", id, user.isPresent() ? "найден" : "не найден");
           return user;
       }catch (Exception e){
           if (transaction != null){
               transaction.rollback();
           }
           log.error("Failed to find user with id: {}",id,e);
           throw new RuntimeException("Failed to find user with id: " +id,e);
       }
   }

    /**
     * Извлекает список всех пользователей из базы данных.
     *
     * @return список всех пользователей
     * @throws RuntimeException если произошла ошибка при получении пользователей
     */
    @Override
   public List<User> findAll(){
       Transaction transaction = null;

       try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           transaction = session.beginTransaction();

           List<User> users = session.createQuery("From User", User.class).list();
            transaction.commit();
            log.info("Успешно извлечено пользователей из БД: {}", users.size());
            return users;
       }catch (Exception e){
           if (transaction != null){
               transaction.rollback();
           }
           log.error("Failed to find all users",e);
           throw new RuntimeException("Failed to find all users",e);
       }
   }
    /**
     * Обновляет существующего пользователя в базе данных.
     *
     * @param object пользователь с обновлёнными данными
     * @return обновлённый пользователь
     * @throws RuntimeException если произошла ошибка при обновлении
     */
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
    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @throws RuntimeException если пользователь не найден или произошла ошибка при удалении
     */
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
