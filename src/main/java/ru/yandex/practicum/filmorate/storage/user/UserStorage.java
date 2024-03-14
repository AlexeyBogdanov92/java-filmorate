package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    public List<User> getAllUsers();

    public User getUserById(Integer id);

    public User postUser(User user);

    public User putUser(User user);

    public Map<String, String> deleteUserById(Integer id);

    public Map<String, String> deleteAllUsers();
}
