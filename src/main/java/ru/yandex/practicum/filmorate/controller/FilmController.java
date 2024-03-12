package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final LocalDate minDate = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        film.generateId();

        validateFilm(film);

        films.put(film.getId(), film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Films id == null");
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Не удалось обновить фильм, " +
                    "т.к. фильм по указанному id не найден.");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Изменили фильм: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть более 200 символов!");
        }
        if (film.getDuration() < 1) {
            throw new ValidationException("продолжительность фильма должна быть положительной!");
        }
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года!");
        }

    }

    public void cleanFilms() {
        films.clear();
    }

}
