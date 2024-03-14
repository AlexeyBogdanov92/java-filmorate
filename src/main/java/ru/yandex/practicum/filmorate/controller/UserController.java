package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(name = "id") Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // @ExceptionHandler
    public User postUser(@Valid @RequestBody User user) {
        return userService.postUser(user);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.putUser(user);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteUserById(@PathVariable(name = "id") Integer id) {
        return userService.deleteUserById(id);
    }

    @DeleteMapping
    public Map<String, String> deleteAllUsers() {
        return userService.deleteAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable(name = "id") Integer userId,
                          @PathVariable(name = "friendId") Integer friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable(name = "id") Integer userId,
                             @PathVariable(name = "friendId") Integer friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable(name = "id") Integer userId) {
        return userService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendList(@PathVariable(name = "id") Integer userId,
                                          @PathVariable(name = "otherId") Integer otherId) {
        return userService.getCommonFriendList(userId, otherId);
    }
}