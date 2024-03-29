package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(name = "id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        return filmService.putFilm(film);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteFilmById(@PathVariable(name = "id") Integer filmId) {
        return filmService.deleteFilmById(filmId);
    }

    @DeleteMapping
    public Map<String, String> deleteAllFilms() {
        return filmService.deleteAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable(name = "id") Integer filmId,
                              @PathVariable(name = "userId") Integer userId) {
        return filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteFilmFromFilm(@PathVariable(name = "id") Integer filmId,
                                   @PathVariable(name = "userId") Integer userId) {
        return filmService.deleteLikeFromFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(name = "count", defaultValue = "10") String count) {
        Integer intCount = Integer.parseInt(count);
        return filmService.getPopularFilms(intCount);
    }
}