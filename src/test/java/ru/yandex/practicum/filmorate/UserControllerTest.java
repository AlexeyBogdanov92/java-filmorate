package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    @Qualifier("inMemoryUserStorage")
    private UserStorage userStorage;
    private UserService userService;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;


    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        user1 = new User(1, "test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10), new HashSet<>());
        user2 = new User(2, "test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12), new HashSet<>());
        user3 = new User(3, "test3@test3.ru",
                "testLogin3", "Test-name3",
                LocalDate.of(1993, 1, 16), new HashSet<>());
        user4 = new User(4, "test4@test4.ru",
                "testLogin4", "Test-name4",
                LocalDate.of(1978, 7, 23), new HashSet<>());
        user5 = new User(5, "test5@test5.ru",
                "testLogin5", "Test-name5",
                LocalDate.of(1996, 11, 20), new HashSet<>());
        user6 = new User(6, "test6@test6.ru",
                "testLogin6", "Test-name6",
                LocalDate.of(1969, 6, 16), new HashSet<>());
    }

    @AfterEach
    void clean() {
        userService.deleteAllUsers();
        User.resetCountID();
    }

    @Test
    void createUserValid() throws ValidationException {
        userService.validate(user1, "добавить");
    }

    @Test
    void createUserInvalid() {
        final User user = User.builder().build();
        Exception exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }

    @Test
    void getUserByIdValid() {
        userService.postUser(user1);
        final User otherUser = userService.getUserById(1);
        assertNotNull(otherUser);
    }

    @Test
    void getUserByIdInvalid() {
        userService.postUser(user1);

        Exception exc = assertThrows(NotFoundException.class, () -> userService.getUserById(2));
        assertEquals("Пользователь с id: 2 не найден", exc.getMessage());

        final List<User> userList = userService.getAllUsers();
        assertNotEquals(userList.size(), 2);
        assertFalse(userList.contains(user2));
    }

    @Test
    void getUsers() {
        assertEquals(userService.getAllUsers().size(), 0);
        userService.postUser(user1);
        assertEquals(userService.getAllUsers().size(), 1);
    }

    @Test
    void updateUserValid() {
        userService.postUser(user1);
        user1.setName("New name");
        userService.putUser(user1);
        assertEquals("New name", user1.getName());
    }

    @Test
    void updateUserInvalid() {
        final User user = User.builder().build();
        Exception exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }

    @Test
    void deleteUserByIdValid() {
        userService.postUser(user1);
        Map<String, String> response = userService.deleteUserById(1);
        assertEquals(response, Map.of("info", String.format("Пользователь по id: 1 успешно удален")));
        assertEquals(userService.getAllUsers().size(), 0);
    }

    @Test
    void deleteUserByIdInvalid() {
        Exception exc = assertThrows(NotFoundException.class, () -> userService.getUserById(1));
        assertEquals("Пользователь с id: 1 не найден", exc.getMessage());
        assertFalse(userService.getAllUsers().remove(user1));
    }

    @Test
    void deleteAllUsers() {
        userService.postUser(user1);
        userService.postUser(user2);
        userService.postUser(user3);
        Map<String, String> response = userService.deleteAllUsers();
        assertEquals(response, Map.of("info", String.format("Все пользователи успешно удалены")));
        assertEquals(userService.getAllUsers().size(), 0);
    }

    @Test
    void addFriend() {
        userService.postUser(user1);
        userService.postUser(user2);
        userService.addFriend(1, 2);

        assertEquals(user1.getFriends().size(), 1);
        assertFalse(user1.getFriends().isEmpty());
        assertEquals(user1.getFriends(), Set.of(2));
        assertEquals(user2.getFriends().size(), 1);
        assertFalse(user2.getFriends().isEmpty());
        assertEquals(user2.getFriends(), Set.of(1));
    }

    @Test
    void deleteFriend() {
        userService.postUser(user1);
        userService.postUser(user2);
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);

        assertEquals(user1.getFriends().size(), 0);
        assertTrue(user1.getFriends().isEmpty());
        assertEquals(user1.getFriends(), Set.of());
        assertEquals(user2.getFriends().size(), 0);
        assertTrue(user2.getFriends().isEmpty());
        assertEquals(user2.getFriends(), Set.of());
    }

    @Test
    void tests() {
        userService.postUser(user1);
        userService.postUser(user2);
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);

        assertEquals(user1.getFriends().size(), 0);
        assertTrue(user1.getFriends().isEmpty());
        assertEquals(user1.getFriends(), Set.of());
        assertEquals(user2.getFriends().size(), 0);
        assertTrue(user2.getFriends().isEmpty());
        assertEquals(user2.getFriends(), Set.of());
    }

}