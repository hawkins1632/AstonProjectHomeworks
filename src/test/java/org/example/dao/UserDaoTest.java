package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.DBException;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserDaoTest extends AbstractDaoTest {

    private UserDao userDao;

    @BeforeEach
    void userDaoTestSetUp() {
        userDao = UserDao.getInstance();
        cleanDatabase();
    }


    private void cleanDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("Should update user successfully when all fields are valid")
    void update_shouldUpdateUser_whenUserExists() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 30));

        saved.setName("Ivan Updated");
        saved.setEmail("ivan.updated@test.com");
        saved.setAge(35);

        User updated = userDao.update(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Ivan Updated", updated.getName());
        assertEquals("ivan.updated@test.com", updated.getEmail());
        assertEquals(35, updated.getAge());
        assertEquals(saved.getCreatedAt(), updated.getCreatedAt());

        Optional<User> fromDb = userDao.findById(saved.getId());
        assertTrue((fromDb).isPresent());
        assertEquals("Ivan Updated", fromDb.get().getName());
    }

    @Test
    @DisplayName("Should update only the name when other fields remain unchanged")
    void update_should_updateOnlyName_when_onlyNameChanged() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 30));

        saved.setName("Ivan Updated");
        User updated = userDao.update(saved);


        assertEquals("Ivan Updated", updated.getName());
        assertEquals(saved.getEmail(), updated.getEmail());
        assertEquals(saved.getAge(), updated.getAge());
    }

    @Test
    @DisplayName("Should update only the email when other fields remain unchanged")
    void update_should_updateOnlyEmail_when_onlyEmailChanged() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 30));

        saved.setEmail("ivan.updated@test.com");
        User updated = userDao.update(saved);


        assertEquals(saved.getName(), updated.getName());
        assertEquals("ivan.updated@test.com", updated.getEmail());
        assertEquals(saved.getAge(), updated.getAge());
    }

    @Test
    @DisplayName("Should update only the age when other fields remain unchanged")
    void update_should_updateOnlyAge_when_onlyAgeChanged() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 30));

        saved.setAge(99);
        User updated = userDao.update(saved);


        assertEquals(saved.getName(), updated.getName());
        assertEquals(saved.getEmail(), updated.getEmail());
        assertEquals(99, updated.getAge());
    }

    @Test
    @DisplayName("Should throw DBException when updating non-existing user")
    void update_shouldThrowException_whenUserDoesNotExist() {
        User ghost = new User("Ghost", "ghost@test.com", 30);
        ghost.setId(9999L);

        assertThrows(DBException.class, () -> userDao.save(ghost));
    }

    @Test
    @DisplayName("Should throw DBException when updating user with duplicate email")
    void update_shouldThrowException_whenEmailIsDuplicate() {
        userDao.save(new User("Ivan", "ivan@test.com", 25));
        User second = userDao.save(new User("Petr", "petr@test.com", 30));

        second.setEmail("petr@test.com");

        assertThrows(DBException.class, () -> userDao.save(second));
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void delete_shouldRemoveUser_whenUserExists() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 30));

        userDao.delete(saved.getId());

        assertTrue(userDao.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existing user")
    void delete_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userDao.delete(9999L));
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid ID")
    void delete_should_throwException_when_idIsInvalid() {
        assertThrows(Exception.class, () -> userDao.delete(null));
        assertThrows(Exception.class, () -> userDao.delete(-1L));
        assertThrows(Exception.class, () -> userDao.delete(0L));
    }

    @Test
    @DisplayName("Should not affect other users when deleting one user")
    void delete_shouldNotAffectOtherUsers() {
        User user1 = userDao.save(new User("Ivan", "ivan@test.com", 25));
        User user2 = userDao.save(new User("Petr", "petr@test.com", 30));

        userDao.delete(user1.getId());

        assertTrue(userDao.findById(user2.getId()).isPresent());
        assertEquals(1, userDao.findAll().size());
    }
}