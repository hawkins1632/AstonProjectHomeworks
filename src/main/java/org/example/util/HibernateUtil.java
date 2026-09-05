package org.example.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Утилитный класс для настройки и управления фабрики сессий Hibernate.
 * Обеспечивает создание единственного экземпляра SessionFactory, который используется всеми DAO
 * для взаимодействия с базой данных PostgreSQL.
 *
 * @author Морозов Павел
 */
@Slf4j
public class HibernateUtil {

    // Единственный неизменяемый экземпляр фабрики сессий для приложения
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Внутренний приватный метод дял инициализации конфигурации и сборки SessionFactory.
     * Автоматически считывает настройки из файла hibernate.cfg.xml в ресурсах.
     *
     * @return настроенный и готовый к работе объект SessionFactory
     * @throws ExceptionInInitializerError если произошла ошибка подключения к БД
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Создаёт объект конфигурации, читаем xml файл и строим фабрику
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception ex){
            //Если БД отключена или пароль неверный - логирует критическую ошибку в консоль
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Закрывает фабрику сессий и освобождает все занятые ресурсы и соединения.
     * Должен вызываться при завершении рабботы консольного приложения.
     */
    public static void shutdown() {
        if (sessionFactory != null && sessionFactory.isClosed()) {
            log.info("Закрытие Hibernate SessionFactory и освобождение ресурсов.");
            sessionFactory.close();
        }
    }
}
