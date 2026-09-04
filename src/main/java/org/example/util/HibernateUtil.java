package org.example.util;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Утильный класс для настройки и управления фабрики сессий Hibernate.
 * Обеспечивает создание единственного экземпляра SessionFactory(паттерн Singleton)
 * для обеспечения взаимодействия с базой данных PostgreSQL.
 *
 * @author Морозов Павел
 */
public class HibernateUtil {
    // Единственный неизменяемый экземпляр фабрики сессий для приложения
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Внутренний приватный метод дял инициализации конфигурации и сборки SessionFactory.
     * Автоматически считывает настройки из файла hibernate.cfg.xml в ресурсах.
     *
     * @return настроенный и готовый к работе объект SessionFactory
     * @throws ExceptionInInitializerError если произошла ошибка подключения к БД
     */
    private static SessionFactory buildSessionFactory(){
        try{
            // Создаём объект конфигурации, читаем xml файл и строим фабрику
            return new Configuration().configure().buildSessionFactory();
        }catch (Throwable ex){
            //Если БД отключена или пароль неверный - логируем критическую ошибку в консоль
            System.err.println("Initial Session Factory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Публичный статический метод для получения доступа к глобальной фабрике сессий.
     * Используется во всех DAO классах для открытия сессий.
     *
     * @return экземпляр SessionFactory
     */
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}
