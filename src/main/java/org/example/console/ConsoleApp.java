package org.example.console;

import org.example.util.InputUtils;

import java.util.Scanner;


/**
 * Представляет основное консольное приложение, отвечающее за взаимодействие с пользователем.
 */
public class ConsoleApp {
    private final Scanner scanner;

    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            try {
                ConsoleAction.printMenu();
                int choice = InputUtils.readIntInRange(scanner,7);

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
    }
}