package org.example.console;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dao.UserDao;
import org.example.model.User;
import org.example.service.UserServiceImpl;
import org.example.util.InputUtils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
/**
 * Перечисление, представляющее доступные действия в консольном приложении.
 * Каждая команда инкапсулирует свою логику взаимодействия с пользователем
 * и вызовы соответствующих методов уровня доступа к данным (DAO).
 */
@Getter
@AllArgsConstructor
public enum ConsoleAction {

    CREATE(1, "Create user") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter name: ");
            String name = InputUtils.readString(scanner);

            System.out.print("Enter email: ");
            String email = InputUtils.readString(scanner);

            System.out.print("Enter age: ");
            int age = InputUtils.readPositiveInt(scanner);

            try {
                User savedUser = UserServiceImpl.getInstance().createUser(name,email,age);
                System.out.println("User created successfully!");
                System.out.println(savedUser);
            } catch (Exception e) {
                System.out.println("Execution failed: " + e.getMessage());
            }
        }
    },

    READ(2, "Get user by ID") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                User user = UserServiceImpl.getInstance().getUserById(id);
                System.out.println("User found: ");
                System.out.println(user);
            }  catch (Exception e) {
                System.out.println("Execution failed: " + e.getMessage());
            }
        }
    },

    READ_ALL(3, "Get all users") {
        @Override
        public void execute(Scanner scanner) {
            try {
                List<User> users = UserServiceImpl.getInstance().getAllUsers();
                if (users.isEmpty()) {
                    System.out.println("No users found.");
                } else {
                    System.out.println("─".repeat(60));
                    System.out.println("Total users: " + users.size());
                    System.out.println("─".repeat(60));
                    users.forEach(System.out::println);
                    System.out.println("─".repeat(60));
                }
            } catch (Exception e) {
                System.out.println("Execution failed: " + e.getMessage());
            }
        }
    },

    UPDATE(4, "Update user") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                User user = UserServiceImpl.getInstance().getUserById(id);
                System.out.println("Current user data:");
                System.out.println(user);
                System.out.println("─".repeat(40));

                System.out.print("Enter new name: ");
                String newName = InputUtils.readString(scanner);

                System.out.print("Enter new email: ");
                String newEmail = InputUtils.readString(scanner);

                System.out.print("Enter new age: ");
                int newAge = InputUtils.readPositiveInt(scanner);

                User updatedUser = UserServiceImpl.getInstance().updateUser(id,newName, newEmail, newAge);
                System.out.println("User updated successfully!");
                System.out.println(updatedUser);

            } catch (Exception e) {
                System.out.println("Execution failed: " + e.getMessage());
            }
        }
    },

    DELETE(5, "Delete user") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                User user = UserServiceImpl.getInstance().getUserById(id);
                System.out.println("User to delete:");
                System.out.println(user);

                System.out.print("Are you sure you want to delete this user? (y/n): ");
                if (InputUtils.readBoolean(scanner)) {
                    UserServiceImpl.getInstance().deleteUser(id);
                    System.out.println("User deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }

            } catch (Exception e) {
                System.out.println("Execution failed: " + e.getMessage() );
            }
        }
    },

    EXIT(0, "Exit") {
        @Override
        public void execute(Scanner scanner) {
        }
    };

    private final int code;
    private final String description;

    /**
     * Выполняет действие
     */
    public abstract void execute(Scanner scanner);

    /**
     * Находит действие по коду
     */
    public static ConsoleAction fromCode(int code) {
        for (ConsoleAction action : values()) {
            if (action.code == code) {
                return action;
            }
        }
        return null;
    }

    /**
     * Выводит меню
     */
    public static void printMenu() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("          USER SERVICE CONSOLE APP");
        System.out.println("═".repeat(50));
        for (ConsoleAction action : values()) {
            System.out.printf("  %s. %s%n", action.code, action.description);
        }
        System.out.println("═".repeat(50));
        System.out.print("Enter your choice: ");
    }
}
