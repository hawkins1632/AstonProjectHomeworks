package org.example.console;

import org.example.exception.DBException;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleActionTest {
    @Mock
    private UserService userService;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private Scanner scannerOf(String input) {
        return new Scanner(input);
    }

    private String stdout() {
        return out.toString();
    }

    private String stderr() {
        return err.toString();
    }

    private User sampleUser() {
        User user = new User("Ivan", "ivan@test.com", 30);
        user.setId(1L);
        return user;
    }

    @Test
    @DisplayName("CREATE: should call createUser with parsed input")
    void create_shouldCallCreateUser() {
        Scanner scanner = scannerOf("Ivan\nivan@test.com\n30\n");
        when(userService.createUser(any(User.class))).thenReturn(sampleUser());

        ConsoleAction.CREATE.execute(scanner, userService);
        for (String s : Set.of("User created successfully!",
                "User | ID: 1 | Name: Ivan | Email: ivan@test.com | Age: 30 ")) {
            assertTrue(stdout().contains(s));
        }
    }

    @Test
    @DisplayName("CREATE: should print error when service throws")
    void create_shouldPrintError_whenServiceFails() {
        Scanner scanner = scannerOf("Ivan\nivan@test.com\n30\n");
        when(userService.createUser(any(User.class)))
                .thenThrow(new DBException("Error"));

        ConsoleAction.CREATE.execute(scanner, userService);
        for (String s : Set.of("Execution failed", "Error")) {
            assertTrue(stderr().contains(s));
        }
    }

    @Test
    @DisplayName("READ: should call getUserById with parsed id")
    void read_shouldCallGetUserById() {
        Scanner scanner = scannerOf("1\n");
        when(userService.getUserById(1L)).thenReturn(sampleUser());

        ConsoleAction.READ.execute(scanner, userService);

        verify(userService).getUserById(1L);
        for (String s : Set.of("User found",
                "User | ID: 1 | Name: Ivan | Email: ivan@test.com | Age: 30 ")) {
            assertTrue(stdout().contains(s));
        }
    }

    @Test
    @DisplayName("READ: should print error when user not found")
    void read_shouldPrintError_whenUserNotFound() {
        Scanner scanner = scannerOf("99\n");
        when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException(99L));

        ConsoleAction.READ.execute(scanner, userService);

        for (String s : Set.of("Execution failed", "99")) {
            assertTrue(stderr().contains(s));
        }
    }

    @Test
    @DisplayName("READ_ALL: should print 'No users found' when list is empty")
    void readAll_shouldPrintNoUsers_whenListIsEmpty() {
        Scanner scanner = scannerOf("");
        when(userService.getAllUsers()).thenReturn(List.of());

        ConsoleAction.READ_ALL.execute(scanner, userService);
        assertTrue(stdout().contains("No users found"));
    }

    @Test
    @DisplayName("READ_ALL: should print all users with total count")
    void readAll_shouldPrintAllUsers() {
        Scanner scanner = scannerOf("");
        User user2 = new User("Petr", "petr@test.com", 35);
        user2.setId(2L);
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser(), user2));

        ConsoleAction.READ_ALL.execute(scanner, userService);

        for (String s : Set.of("Total users: 2",
                "User | ID: 1 | Name: Ivan | Email: ivan@test.com | Age: 30 ",
                "User | ID: 2 | Name: Petr | Email: petr@test.com | Age: 35 ")) {
            assertTrue(stdout().contains(s));
        }
    }

    @Test
    @DisplayName("READ_ALL: should print error when service throws")
    void readAll_shouldPrintError_whenServiceFails() {
        Scanner scanner = scannerOf("");
        when(userService.getAllUsers())
                .thenThrow(new DBException("Error"));

        ConsoleAction.READ_ALL.execute(scanner, userService);

        for (String s : Set.of("Execution failed", "Error")) {
            assertTrue(stderr().contains(s));
        }
    }

    @Test
    @DisplayName("UPDATE: should call getUserById then updateUser with new data")
    void update_shouldCallGetUserByIdAndUpdateUser() {
        Scanner scanner = scannerOf("1\nPetr\npetr@test.com\n35\n");
        when(userService.getUserById(1L)).thenReturn(sampleUser());
        User user2 = new User("Petr", "petr@test.com", 35);
        user2.setId(1L);
        when(userService.updateUser(any(User.class))).thenReturn(user2);

        ConsoleAction.UPDATE.execute(scanner, userService);

        verify(userService).getUserById(1L);
        verify(userService).updateUser(user2);
        for (String s : Set.of("User updated successfully!",
                "User | ID: 1 | Name: Petr | Email: petr@test.com | Age: 35 ")) {
            assertTrue(stdout().contains(s));
        }
    }

    @Test
    @DisplayName("UPDATE: should not call updateUser when user not found")
    void update_shouldNotCallUpdateUser_whenUserNotFound() {
        Scanner scanner = scannerOf("99\nIvan\nivan@test.com\n35\n");
        when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException(99L));

        ConsoleAction.UPDATE.execute(scanner, userService);

        verify(userService, never()).updateUser(any());
        assertTrue(stderr().contains("99"));
    }

    @Test
    @DisplayName("DELETE: should call deleteUser when user confirms")
    void delete_shouldCallDeleteUser_whenConfirmed() {
        Scanner scanner = scannerOf("1\ny\n");
        when(userService.getUserById(1L)).thenReturn(sampleUser());

        ConsoleAction.DELETE.execute(scanner, userService);

        verify(userService).deleteUser(1L);
        assertTrue(stdout().contains("User deleted successfully!"));
    }

    @Test
    @DisplayName("DELETE: should not call deleteUser when user cancels")
    void delete_shouldNotCallDeleteUser_whenCancelled() {
        Scanner scanner = scannerOf("1\nn\n");
        when(userService.getUserById(1L)).thenReturn(sampleUser());

        ConsoleAction.DELETE.execute(scanner, userService);

        verify(userService, never()).deleteUser(anyLong());
        assertTrue(stdout().contains("Deletion cancelled."));
    }

    @Test
    @DisplayName("DELETE: should not call deleteUser when user not found")
    void delete_shouldNotCallDeleteUser_whenUserNotFound() {
        Scanner scanner = scannerOf("99\ny\n");
        when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException(99L));

        ConsoleAction.DELETE.execute(scanner, userService);

        verify(userService, never()).deleteUser(anyLong());
        assertTrue(stderr().contains("99"));
    }

    @Test
    @DisplayName("DELETE: should print user info before asking for confirmation")
    void delete_shouldPrintUserBeforeConfirmation() {
        Scanner scanner = scannerOf("1\nn\n");
        when(userService.getUserById(1L)).thenReturn(sampleUser());

        ConsoleAction.DELETE.execute(scanner, userService);

        for (String s : Set.of("User to delete",
                "User | ID: 1 | Name: Ivan | Email: ivan@test.com | Age: 30 ",
                "Are you sure you want to delete this user? (y/n):")) {
            assertTrue(stdout().contains(s));
        }
    }

    @Test
    @DisplayName("EXIT: should do nothing")
    void exit_shouldDoNothing() {
        Scanner scanner = scannerOf("");

        ConsoleAction.EXIT.execute(scanner, userService);

        verifyNoInteractions(userService);
        assertTrue(stdout().isEmpty());
        assertTrue(stderr().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "0, EXIT",
            "1, CREATE",
            "2, READ",
            "3, READ_ALL",
            "4, UPDATE",
            "5, DELETE"
    })
    @DisplayName("fromCode: should return matching action for valid code")
    void fromCode_shouldReturnAction_whenCodeIsValid(int code, String expectedName) {
        ConsoleAction action = ConsoleAction.fromCode(code);

        assertNotNull(action);
        assertEquals(expectedName, action.name());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 6, 100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    @DisplayName("fromCode: should return null for unknown code")
    void fromCode_shouldReturnNull_whenCodeIsUnknown(int code) {
        assertNull(ConsoleAction.fromCode(code));
    }


}