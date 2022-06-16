package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Locale;
import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    static User user;

    @BeforeEach
     void setUp() {
        user = new User();
        user.setUsername("Username1");
        user.setName("Name1");
        user.setPassword("password1");
        user.setRole(Role.ADMINISTRATOR);
        user.setAccountNonLocked(true);
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    @DisplayName("The count of the table must be equal 0")
    void test1() {
        long actual = repository.count();
        long expected = 0;
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("The count of the table must be equal 1")
    void test2() {
        repository.save(user);
        long actual = repository.count();
        long expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("User must get id after persisting")
    void test3() {
        assertNull(user.getId());
        repository.save(user);

        assertNotNull(user.getId());
    }

    @Test
    @DisplayName("User must exists by username in database")
    void test4() {
        repository.save(user);

        Optional<User> exists = repository.findByUsernameIgnoreCase(user.getUsername().toUpperCase(Locale.ROOT));

        assertTrue(exists.isPresent());
    }

    @Test
    @DisplayName("User must be found in database after persisting")
    void test5() {
        repository.save(user);

        User actualUser = repository.findByUsernameIgnoreCase(user.getUsername().toUpperCase(Locale.ROOT)).get();

        assertEquals(user, actualUser);
    }

    @Test
    @DisplayName("The count of rows in database must be 0 after deleting user")
    void test6() {
        assertEquals(0, repository.count());
        repository.save(user);
        assertEquals(1, repository.count());

        repository.deleteByUsernameIgnoreCase(user.getUsername());

        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("Finding user with wrong username must return empty optional")
    void test7() {
        Optional<User> userOptional = repository.findByUsernameIgnoreCase("userNAME2");

        assertTrue(userOptional.isEmpty());
    }
}
