package org.example;

import org.example.console.ConsoleApp;
import org.example.util.HibernateUtil;

public class Main {
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
