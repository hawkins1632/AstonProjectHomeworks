package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User("Ivan", "ivan@test.com", 30);
        validUser.setId(1L);
    }

    @Test
    @DisplayName("updateUser: should update user when data is valid")
    void updateUser_shouldUpdateUser_whenDataIsValid() {
        when(userDao.update(validUser)).thenReturn(validUser);

        User result = userService.updateUser(validUser);

        verify(userDao).update(validUser);
        assertEquals(validUser, result);
    }

    @Test
    @DisplayName("updateUser: should throw IllegalArgumentException when user is null")
    void updateUser_shouldThrowException_whenUserIsNull() {
        verifyNoInteractions(userDao);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L})
    @DisplayName("updateUser: should throw IllegalArgumentException when id is invalid")
    void updateUser_shouldThrowException_whenIdIsInvalid(Long invalidId) {
        validUser.setId(invalidId);

        verifyNoInteractions(userDao);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(validUser));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("updateUser: should throw IllegalArgumentException when name is invalid")
    void updateUser_shouldThrowException_whenNameIsInvalid(String invalidName) {
        validUser.setName(invalidName);

        verifyNoInteractions(userDao);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(validUser));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-an-email", "not-an-email@test", "not-an-email@test.a", "not-an-email.ru"})
    @DisplayName("updateUser: should throw IllegalArgumentException when email is invalid")
    void updateUser_shouldThrowException_whenEmailIsInvalid(String invalidEmail) {
        validUser.setEmail(invalidEmail);

        verifyNoInteractions(userDao);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(validUser));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1})
    @DisplayName("updateUser: should throw IllegalArgumentException when age is invalid")
    void updateUser_shouldThrowException_whenAgeIsInvalid(Integer invalidAge) {
        validUser.setAge(invalidAge);

        verifyNoInteractions(userDao);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(validUser));
    }

    @Test
    @DisplayName("deleteUser: should call DAO when id is valid")
    void deleteUser_shouldCallDaoDelete_whenIdIsValid() {
        userService.deleteUser(1L);

        verify(userDao).delete(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L})
    @DisplayName("deleteUser: should throw IllegalArgumentException when id is invalid")
    void deleteUser_shouldThrowException_whenIdIsInvalid(Long invalidId) {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(invalidId));

        verifyNoInteractions(userDao);
    }
}