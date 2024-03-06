package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController controller = new UserController();
    private final User user1 = new User(11, "test@test.ru",
            "testLogin", "Test-name", LocalDate.of(2015, 11, 10));
    private final User user2 = new User(12, "test@test1.ru",
            "testLogin1", "Test-name1", LocalDate.of(2012, 10, 11));

    @AfterEach
    void clean() {
        controller.cleanUsers();
    }

    @Test
    void getAllUsers() {
        assertEquals(controller.getAllUsers().size(), 0);
        controller.postUser(user1);
        controller.postUser(user2);
        assertEquals(controller.getAllUsers().size(), 2);
    }

    @Test
    void addUserValid() {
        controller.postUser(user1);
        assertEquals(controller.getAllUsers().size(), 1);
    }

    @Test
    void addUserInValidLogin() {
        user1.setLogin("");
        Exception exc = assertThrows(ValidationException.class, () -> controller.postUser(user1));
        assertEquals("Логин не может быть пустым и содержать пробелы!", exc.getMessage());
    }

    @Test
    void addUserInValidMail() {
        user1.setEmail("");
        Exception exc = assertThrows(ValidationException.class, () -> controller.postUser(user1));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exc.getMessage());
    }

    @Test
    void updateUserValid() throws ValidationException {
        controller.postUser(user1);
        user1.setName("Новое имя");
        controller.putUser(user1);
        assertEquals("Новое имя", user1.getName());
    }


}
