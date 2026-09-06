package org.example;

import org.example.console.ConsoleApp;
import org.example.util.HibernateUtil;

/**
 * Главный запускаемый класс приложения.
 * Инициализирует и запускает консольный интерфейс для управления пользователями,
 * а также производит освобождение ресурсов БД при завершении работы.
 */
public class Main {
    /**
     * Точка входа в приложение.
     * Создает экземпляр консольного менеджера и запускает его цикл.
     * В блоке {@code finally} происходит закрытие фабрики сессий Hibernate.
     *
     * @param args аргументы строки( не используются)
     */
    public static void main(String[] args) {
        try {
            ConsoleApp consoleManager = new ConsoleApp();
            consoleManager.start();

        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
