package org.example.console;

import org.example.util.InputUtils;

import java.util.Scanner;


/**
 * Представляет основное консольное приложение, отвечающее за взаимодействие с пользователем.
 */
public class ConsoleApp {
    private final Scanner scanner;

    /**
     * Создаёт новый экземпляр приложения и инициализирует сканер консольного ввода.
     */
    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запускает бесконечный цикл обработки пользовательских команд.
     * Метод считывает выбор пользователя, валидирует его с помощью {@link InputUtils}
     * и передаёт управление соответствующему действию {@link ConsoleAction}.
     * При выборе пункта выхода гарантированно освобождает ресурсы сканера.
     */
    public void start() {
        try {
            while (true) {
                try {
                    ConsoleAction.printMenu();
                    int choice = InputUtils.readIntInRange(scanner, 7);

                    ConsoleAction action = ConsoleAction.fromCode(choice);
                    if (action == null) {
                        System.out.println("Invalid option. Please try again.");
                        continue;
                    } else if (action == ConsoleAction.EXIT) {
                        return;
                    }
                    action.execute(scanner);

                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }
}