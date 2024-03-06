package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Количество пользователей " + users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        user.generateId();

        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }
        validateUser(user);

        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен!", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Не удалось обновить пользователя, " +
                    "т.к. пользователь по указанному id не найден.");
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} изменен!", user);
        return user;
    }

    private void validateUser(User user) {

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }

    public void cleanUsers() {// Метод для тестирования
        users.clear();
    }

}
