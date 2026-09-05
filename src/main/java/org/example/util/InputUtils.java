package org.example.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InputUtils {

    /**
     * Считывает с пользователя положительное целое число.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return положительное целое число, введённое пользователем
     */
    public static int readPositiveInt(Scanner scanner) {
        while (true) {
            int value = readInt(scanner);
            if (value > 0) {
                return value;
            }
            System.out.println("The number must be greater than 0.");
        }
    }

    /**
     * Считывает с пользователя целое число.
     * Повторяет запрос до тех пор, пока не будет введено корректное целое число.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return введённое целое число
     */
    public static int readInt(Scanner scanner) {
        while (true) {
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();

                return value;
            }
            System.out.println("Please enter an integer.");
            scanner.nextLine();
        }
    }

    /**
     * Считывает с пользователя целое число типа long.
     * Повторяет запрос до тех пор, пока не будет введено корректное целое число типа long.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return введённое длинное целое число
     */
    public static long readLong(Scanner scanner) {
        while (true) {
            if (scanner.hasNextLong()) {
                long value = scanner.nextLong();
                scanner.nextLine();

                return value;
            }
            System.out.println("Please enter an long integer.");
            scanner.nextLine();
        }
    }

    /**
     * Считывает с пользователя положительное целое число типа long.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return положительное целое число типа long, введённое пользователем
     */
    public static long readPositiveLong(Scanner scanner) {
        while (true) {
            long value = readLong(scanner);
            if (value > 0) {
                return value;
            }
            System.out.println("The number must be greater than 0.");
        }
    }

    /**
     * Считывает с пользователя непустую строку.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return непустая строка
     */
    public static String readString(Scanner scanner) {
        while (true) {
            String inputString = scanner.nextLine().trim();
            if (!inputString.isEmpty()) {
                return inputString;
            }
            System.out.println("Input string cannot be empty.");
        }
    }

    /**
     * Считывает целое число в указанном диапазоне.
     *
     * @param scanner    сканер, используемый для чтения ввода
     * @param upperBound верхняя граница (исключительно)
     * @return целое число в диапазоне от 0 до {@code upperBound - 1}
     */
    public static int readIntInRange(Scanner scanner, int upperBound) {
        while (true) {
            int value = readInt(scanner);
            if (value >= 0 && value < upperBound) {
                return value;
            }
            System.out.println("The number must be in range [0;" + (upperBound - 1) + "]");
        }
    }

    /**
     * Считывает с пользователя логическое значение.
     * Принимает {@code y}/{@code yes} или {@code n}/{@code no}.
     *
     * @param scanner сканер, используемый для чтения ввода
     * @return {@code true}, если пользователь ввёл yes, иначе {@code false}
     */
    public static boolean readBoolean(Scanner scanner) {
        while (true) {
            String inputString = readString(scanner).toLowerCase();
            if (inputString.equals("y") || inputString.equals("yes")) {
                return true;
            } else if (inputString.equals("n") || inputString.equals("no")) {
                return false;
            }
            System.out.println("The value Y/Yes or N/No must be entered.");
        }
    }
}
