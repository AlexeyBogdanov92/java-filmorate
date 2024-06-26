package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    @Qualifier("inMemoryUserStorage")
    private UserStorage userStorage;
    @Qualifier("inMemoryFilmStorage")
    private FilmStorage filmStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage(userStorage);
        filmService = new FilmService(filmStorage);
    }

    @AfterEach
    void clean() {
        filmService.deleteAllFilms();
        Film.resetCountID();
        User.resetCountID();
    }

    @Test
    void createFilmValid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.validate(film1, "добавить");
    }

    @Test
    void getFilmByIdValid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.postFilm(film1);
        final Film otherFilm = filmService.getFilmById(1);
        assertNotNull(otherFilm);
    }

    @Test
    void getFilmByIdInvalid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.postFilm(film1);

        Exception exc = assertThrows(NotFoundException.class, () -> filmService.getFilmById(2));
        assertEquals("Фильм с id: 2 не найден", exc.getMessage());

        final List<Film> filmList = filmService.getAllFilms();
        assertNotEquals(filmList.size(), 2);

        Film film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        assertFalse(filmList.contains(film2));
    }

    @Test
    void getFilms() {
        assertEquals(filmService.getAllFilms().size(), 0);
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.postFilm(film1);
        assertEquals(filmService.getAllFilms().size(), 1);
    }

    @Test
    void updateFilmValid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.postFilm(film1);
        film1.setDescription("Новое описание фильма Защитник");
        filmService.putFilm(film1);
        assertEquals("Новое описание фильма Защитник", film1.getDescription());
    }

    @Test
    void updateFilmInvalid() {
        final Film film = new Film(1, "Защитник",
                "Описание фильма",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());

        film.setName("");
        Exception exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("Хоббит");
        film.setDescription("x".repeat(201));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. максимальная длина описания 200 символов.",
                exc.getMessage());

        film.setDescription("x".repeat(100));
        film.setReleaseDate(LocalDate.of(1888, 12, 12));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм," +
                " т.к. дата релиза не может быть раньше даты создания кино.", exc.getMessage());

        film.setReleaseDate(LocalDate.of(1988, 12, 12));
        film.setDuration(-1L);
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. продолжительность фильма должна быть положительной.",
                exc.getMessage());
    }

    @Test
    void deleteFilmByIdValid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmService.postFilm(film1);
        Map<String, String> response = filmService.deleteFilmById(1);
        assertEquals(response, Map.of("info", String.format("Фильм по id: 1 успешно удален")));
        assertEquals(filmService.getAllFilms().size(), 0);
    }

    @Test
    void deleteFilmByIdInvalid() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Exception exc = assertThrows(NotFoundException.class, () -> filmService.getFilmById(1));
        assertEquals("Фильм с id: 1 не найден", exc.getMessage());
        assertFalse(filmService.getAllFilms().remove(film1));
    }

    @Test
    void deleteAllFilms() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        Film film3 = new Film(3, "Зеленая миля",
                "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников " +
                        "которого однажды проходит «зеленую милю» по пути к месту казни.",
                LocalDate.of(1999, 12, 6), 189,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        filmService.postFilm(film1);
        filmService.postFilm(film2);
        filmService.postFilm(film3);
        Map<String, String> response = filmService.deleteAllFilms();
        assertEquals(response, Map.of("info", String.format("Все фильмы успешно удалены")));
        assertEquals(filmService.getAllFilms().size(), 0);
    }

    @Test
    void addLikeToFilm() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        filmService.postFilm(film1);
        filmService.postFilm(film2);

        User user1 = new User(1, "test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10), new HashSet<>());
        User user2 = new User(2, "test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12), new HashSet<>());
        userStorage.postUser(user1);
        userStorage.postUser(user2);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        assertEquals(film1.getLikes().size(), 2);
        assertFalse(film1.getLikes().isEmpty());
        assertEquals(film1.getLikes(), Set.of(1, 2));
    }

    @Test
    void deleteLikeFromFilm() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        filmService.postFilm(film1);
        filmService.postFilm(film2);

        User user1 = new User(1, "test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10), new HashSet<>());
        User user2 = new User(2, "test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12), new HashSet<>());
        userStorage.postUser(user1);
        userStorage.postUser(user2);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        filmService.deleteLikeFromFilm(1, 1);
        assertEquals(film1.getLikes().size(), 1);
        assertFalse(film1.getLikes().isEmpty());
        assertEquals(film1.getLikes(), Set.of(2));
    }

    @Test
    void getPopularFilms() {
        Film film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        Film film3 = new Film(3, "Зеленая миля",
                "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников " +
                        "которого однажды проходит «зеленую милю» по пути к месту казни.",
                LocalDate.of(1999, 12, 6), 189,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        Film film4 = new Film(4, "1+1",
                "Аристократ на коляске нанимает в сиделки бывшего заключенного. " +
                        "Искрометная французская комедия с Омаром Си",
                LocalDate.of(2011, 9, 23), 112,
                Set.of(new Genre(4, "Драма"),
                        new Genre(5, "Мелодрама")),
                new MPA(3, "PG-13", 13), new HashSet<>());
        Film film5 = new Film(5, "Побег из Шоушенка",
                "Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника.",
                LocalDate.of(1994, 9, 10), 142,
                Set.of(new Genre(2, "Триллер"),
                        new Genre(6, "Ужасы")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film6 = new Film(6, "Форрест Гамп",
                "Сидя на автобусной остановке, Форрест Гамп — не очень умный, но добрый и открытый " +
                        "парень — рассказывает случайным встречным историю своей необыкновенной жизни.",
                LocalDate.of(1994, 6, 23), 142,
                Set.of(new Genre(1, "Комедия")),
                new MPA(1, "G", 0), new HashSet<>());
        Film film7 = new Film(7, "Интерстеллар",
                "Когда засуха, пыльные бури и вымирание растений приводят человечество к " +
                        "продовольственному кризису, коллектив исследователей и учёных отправляется сквозь " +
                        "червоточину.",
                LocalDate.of(2014, 10, 26), 169,
                Set.of(new Genre(2, "Триллер"),
                        new Genre(4, "Драма")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        Film film8 = new Film(8, "Унесённые призраками",
                "Тихиро с мамой и папой переезжает в новый дом. Заблудившись по дороге, они оказываются " +
                        "в странном пустынном городе, где их ждет великолепный пир.",
                LocalDate.of(2001, 7, 20), 125,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        Film film9 = new Film(9, "Властелин колец: Возвращение короля",
                "Повелитель сил тьмы Саурон направляет свою бесчисленную армию под стены Минас-Тирита, " +
                        "крепости Последней Надежды.",
                LocalDate.of(2003, 12, 1), 201,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film10 = new Film(10, "Бойцовский клуб",
                "Страховой работник разрушает рутину своей благополучной жизни. Культовая драма " +
                        "по книге Чака Паланика",
                LocalDate.of(1999, 9, 10), 139,
                Set.of(new Genre(2, "Триллер"),
                        new Genre(6, "Ужасы")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film11 = new Film(11, "Список Шиндлера",
                "Фильм рассказывает реальную историю загадочного Оскара Шиндлера, члена нацистской " +
                        "партии, преуспевающего фабриканта, спасшего во время Второй мировой войны почти 1200 евреев.",
                LocalDate.of(1993, 11, 30), 195,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        Film film12 = new Film(12, "Шрэк",
                "Жил да был в сказочном государстве большой зеленый великан по имени Шрэк. " +
                        "Жил он в гордом одиночестве в лесу, на болоте, которое считал своим.",
                LocalDate.of(2001, 4, 22), 90,
                Set.of(new Genre(2, "Триллер"),
                        new Genre(4, "Драма")),
                new MPA(5, "NC-17", 18), new HashSet<>());

        filmService.postFilm(film1);
        filmService.postFilm(film2);
        filmService.postFilm(film3);
        filmService.postFilm(film4);
        filmService.postFilm(film5);
        filmService.postFilm(film6);
        filmService.postFilm(film7);
        filmService.postFilm(film8);
        filmService.postFilm(film9);
        filmService.postFilm(film10);
        filmService.postFilm(film11);
        filmService.postFilm(film12);

        User user1 = new User(1, "test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10), new HashSet<>());
        User user2 = new User(2, "test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12), new HashSet<>());
        User user3 = new User(3, "test3@test3.ru",
                "testLogin3", "Test-name3",
                LocalDate.of(1993, 1, 16), new HashSet<>());
        User user4 = new User(4, "test4@test4.ru",
                "testLogin4", "Test-name4",
                LocalDate.of(1978, 7, 23), new HashSet<>());
        User user5 = new User(5, "test5@test5.ru",
                "testLogin5", "Test-name5",
                LocalDate.of(1996, 11, 20), new HashSet<>());
        User user6 = new User(6, "test6@test6.ru",
                "testLogin6", "Test-name6",
                LocalDate.of(1969, 6, 16), new HashSet<>());

        userStorage.postUser(user1);
        userStorage.postUser(user2);
        userStorage.postUser(user3);
        userStorage.postUser(user4);
        userStorage.postUser(user5);
        userStorage.postUser(user6);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        filmService.addLikeToFilm(1, 3);
        filmService.addLikeToFilm(1, 4);
        filmService.addLikeToFilm(1, 5);
        filmService.addLikeToFilm(1, 6);

        filmService.addLikeToFilm(3, 1);
        filmService.addLikeToFilm(3, 3);
        filmService.addLikeToFilm(3, 5);

        filmService.addLikeToFilm(5, 1);
        filmService.addLikeToFilm(5, 2);
        filmService.addLikeToFilm(5, 3);
        filmService.addLikeToFilm(5, 4);
        filmService.addLikeToFilm(5, 5);

        filmService.addLikeToFilm(7, 1);
        filmService.addLikeToFilm(7, 2);
        filmService.addLikeToFilm(7, 3);
        filmService.addLikeToFilm(7, 4);

        filmService.addLikeToFilm(11, 1);

        assertEquals(List.of(film1, film5), filmService.getPopularFilms(2));
        assertEquals(List.of(film1, film5, film7, film3, film11),
                filmService.getPopularFilms(5));
    }

}