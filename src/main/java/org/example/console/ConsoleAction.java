package org.example.console;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dao.UserDao;
import org.example.model.User;
import org.example.util.InputUtils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
                User user = new User(name, email, age);
                User savedUser = UserDao.getInstance().save(user);
                System.out.println("User created successfully!");
                System.out.println(savedUser);
            } catch (Exception ignored) {
            }
        }
    },

    READ(2, "Get user by ID") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                Optional<User> userOptional = UserDao.getInstance().findById(id);
                if (userOptional.isPresent()) {
                    System.out.println("User found:");
                    System.out.println(userOptional.get());
                } else {
                    System.out.println("User not found with ID: " + id);
                }
            }  catch (Exception ignored) {
            }
        }
    },

    READ_ALL(3, "Get all users") {
        @Override
        public void execute(Scanner scanner) {
            try {
                List<User> users = UserDao.getInstance().findAll();
                if (users.isEmpty()) {
                    System.out.println("No users found.");
                } else {
                    System.out.println("─".repeat(60));
                    System.out.println("Total users: " + users.size());
                    System.out.println("─".repeat(60));
                    users.forEach(System.out::println);
                    System.out.println("─".repeat(60));
                }
            } catch (Exception ignored) {
            }
        }
    },

    UPDATE(4, "Update user") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                Optional<User> existingUser = UserDao.getInstance().findById(id);
                if (existingUser.isEmpty()) {
                    System.out.println("User not found with ID: " + id);
                    return;
                }

                User user = existingUser.get();
                System.out.println("Current user data:");
                System.out.println(user);
                System.out.println("─".repeat(40));

                System.out.print("Enter new name: ");
                user.setName(InputUtils.readString(scanner));

                System.out.print("Enter new email: ");
                user.setEmail(InputUtils.readString(scanner));

                System.out.print("Enter new age: ");
                user.setAge(InputUtils.readPositiveInt(scanner));

                User updatedUser = UserDao.getInstance().update(user);
                System.out.println("User updated successfully!");
                System.out.println(updatedUser);

            } catch (Exception ignored) {
            }
        }
    },

    DELETE(5, "Delete user") {
        @Override
        public void execute(Scanner scanner) {
            System.out.print("Enter user ID: ");
            long id = InputUtils.readPositiveLong(scanner);

            try {
                Optional<User> user = UserDao.getInstance().findById(id);
                if (user.isEmpty()) {
                    System.out.println("User not found with ID: " + id);
                    return;
                }

                System.out.println("User to delete:");
                System.out.println(user.get());
                System.out.print("Are you sure you want to delete this user? (y/n): ");
                if (InputUtils.readBoolean(scanner)) {
                    UserDao.getInstance().delete(id);
                    System.out.println("User deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }

            } catch (Exception ignored) {
            }
        }
    },

    EXIT(0, "Exit") {
        @Override
        public void execute(Scanner scanner) {
            scanner.close();
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
