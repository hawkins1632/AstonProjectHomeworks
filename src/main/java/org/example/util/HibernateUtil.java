package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитный класс для настройки и управления фабрики сессий Hibernate.
 * Обеспечивает создание единственного экземпляра SessionFactory, который используется всеми DAO
 * для взаимодействия с базой данных PostgreSQL.
 *
 * @author Морозов Павел
 */
public class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
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
        }catch (Exception ex){
            //Если БД отключена или пароль неверный - логируем критическую ошибку в консоль
            log.error("Initial SessionFactory creation failed.", ex);
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
