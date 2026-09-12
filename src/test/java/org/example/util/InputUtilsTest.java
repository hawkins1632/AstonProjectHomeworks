package org.example.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InputUtilsTest {
    @Test
    @DisplayName("readPositiveInt: should return value when input is positive")
    void readPositiveIntValidTest() {

        Scanner scanner = new Scanner("""
                25
                """);

        int value = InputUtils.readPositiveInt(scanner);

        assertEquals(25, value);
    }

    @Test
    @DisplayName("readPositiveInt: should skip invalid values and returns first positive")
    void readPositiveIntInvalidValuesTest() {

        Scanner scanner = new Scanner("""
                -5
                0
                abc
                10
                """);

        int value = InputUtils.readPositiveInt(scanner);

        assertEquals(10, value);
    }

    @Test
    @DisplayName("readInt: should return value when input is a valid integer")
    void readIntValidInputTest() {

        Scanner scanner = new Scanner("""
                -10
                """);

        int value = InputUtils.readInt(scanner);

        assertEquals(-10, value);
    }

    @Test
    @DisplayName("readInt: should skip non-integer input and returns next valid integer")
    void readIntInvalidInputTest() {

        Scanner scanner = new Scanner("""
                abc
                
                
                -10
                """);

        int value = InputUtils.readInt(scanner);

        assertEquals(-10, value);
    }

    @Test
    @DisplayName("readPositiveLong: should return value when input is positive")
    void readPositiveLongValidTest() {

        Scanner scanner = new Scanner("""
                25
                """);

        long value = InputUtils.readPositiveLong(scanner);

        assertEquals(25L, value);
    }

    @Test
    @DisplayName("readPositiveLong: should skip invalid values and returns first positive")
    void readPositiveLongInvalidValuesTest() {

        Scanner scanner = new Scanner("""
                -5
                0
                abc
                10
                """);

        long value = InputUtils.readPositiveLong(scanner);

        assertEquals(10L, value);
    }

    @Test
    @DisplayName("readLong: should return value when input is a valid long")
    void readLongValidInputTest() {

        Scanner scanner = new Scanner("""
                -10
                """);

        long value = InputUtils.readLong(scanner);

        assertEquals(-10L, value);
    }

    @Test
    @DisplayName("readLong: should skip non-numeric input and returns next valid long")
    void readLongInvalidInputTest() {

        Scanner scanner = new Scanner("""
                abc
                
                
                -10
                """);

        long value = InputUtils.readLong(scanner);

        assertEquals(-10L, value);
    }

    @Test
    @DisplayName("readIntInRange: should skip invalid input and returns value within range")
    public void readIntInRangeTest() {
        Scanner scanner = new Scanner("""
                
                string
                
                
                0.25
                -10
                100
                5
                """);
        assertEquals(5, InputUtils.readIntInRange(scanner, 6));
    }

    @Test
    @DisplayName("readString: should skip blank input and returns first non-blank string")
    void readStringEmptyInputTest() {

        Scanner scanner = new Scanner("""
                
                Ivan
                """);

        String value = InputUtils.readString(scanner);

        assertEquals("Ivan", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"y", "Y", "yes", "YES", "Yes"})
    @DisplayName("readBoolean: should return true for affirmative input")
    void readBoolean_shouldReturnTrue_whenInputIsAffirmative(String input) {
        Scanner scanner = new Scanner(input);
        assertTrue(InputUtils.readBoolean(scanner));
    }

    @ParameterizedTest
    @ValueSource(strings = {"n", "N", "no", "NO", "No"})
    @DisplayName("readBoolean: should return false for negative input")
    void readBoolean_shouldReturnFalse_whenInputIsNegative(String input) {
        Scanner scanner = new Scanner(input);
        assertFalse(InputUtils.readBoolean(scanner));
    }
}