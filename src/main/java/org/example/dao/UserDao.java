package org.example.dao;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class UserDao {


    /**
     * Находит пользователя в БД по его уникальному ID
     *
     * @param id уникальный идентификатор пользователя в системе
     * @return объект найденного пользователя User, либо null если запись отсутствует
     */
    public User findById(Long id){
        // Открываем сессию взаимодействия с БД через утильный класс
       try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           // Для чтения транзакции не требуются, используем встроенный метод get
           return session.get(User.class, id);
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
}
