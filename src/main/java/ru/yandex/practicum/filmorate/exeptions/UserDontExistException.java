package ru.yandex.practicum.filmorate.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserDontExistException extends RuntimeException {
    public UserDontExistException() {
    }

    public UserDontExistException(String message) {
        super(message);
    }
}