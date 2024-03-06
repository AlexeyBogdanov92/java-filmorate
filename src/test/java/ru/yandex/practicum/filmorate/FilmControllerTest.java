package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

public class FilmControllerTest {
    private FilmController controller = new FilmController();

    private Film film1 = new Film(21, "film1",
            "описание фильма 1",
            LocalDate.of(2011, 10, 15), 111);
    private Film film2 = new Film(22, "film2",
            "описание фильма 2",
            LocalDate.of(2012, 11, 11), 121);


    @AfterEach
    void clean() {
        controller.cleanFilms();
    }

    @Test
    void postFilmValid() {
        controller.postFilm(film1);
        assertEquals(controller.getAllFilms().size(), 1);
    }

    @Test
    void postFilmInValidName() {
        film1.setName("");
        Exception exc = assertThrows(ValidationException.class, () -> controller.postFilm(film1));
        assertEquals("Название фильма не может быть пустым!", exc.getMessage());
    }

    @Test
    void postFilmInValidDescription() {
        film1.setDescription("«Мстители: Финал»\n" +
                "Пять лет спустя Кэрол Дэнверс улетает на другую планету, Наташа и Стив обсуждают, как Мстители стали" +
                " друг для друга семьей, а крыса в фургончике Луиса случайно активирует переключатель," +
                " который выпускает Скотта Лэнга из Квантового измерения. Убедившись, что Кэсси не пропала от щелчка," +
                " он едет к Мстителям и рассказывает им про Квантовое измерение и то, что можно с его помощью" +
                " отправиться в прошлое и все исправить.\n" +
                "\n" +
                "Тони Старк отказывается помогать в создании машины времени: он живет в уединении с Пеппер," +
                " у него есть дочь — его все устраивает. Тогда они отправляются к Профессору Халку" +
                " (Баннер и Халк слились), который и строит им машину времени, которая, правда, не работает." +
                " Поразмыслив, Старк наконец смоглашается помочь и дорабатывает машину времени — правда," +
                " для ее функционирования нужны квантовые частицы Хэнка Пима, которых осталось мало.\n" +
                "\n" +
                "Наташа находит в Японии грустящего Клинта, который занят истреблением преступников по всей планете," +
                " а Ракета и Халк вербуют Тора, который растолстел, круглые сутки пьет и играет в видеоигры" +
                " с Коргом и Миеком. Он живет в Новом Асгарде, где Валькирия командует оставшимися асгардцами," +
                " пока их царь в депрессии. Команда Мстителей в сборе, но квантовых частиц хватит только на одно" +
                " путешествие для всех, поэтому герои делятся на три команды так, чтобы за одно из путешествий им" +
                " удалось добыть сразу три камня, а за другое — два.");
        Exception exc = assertThrows(ValidationException.class, () -> controller.postFilm(film1));
        assertEquals("Описание фильма не может быть более 200 символов!", exc.getMessage());
    }

    @Test
    void postFilmInValidRelease() {
        film1.setReleaseDate(LocalDate.of(1111, 10, 15));
        Exception exc = assertThrows(ValidationException.class, () -> controller.postFilm(film1));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года!", exc.getMessage());
    }

    @Test
    void postFilmInValidDuration() {
        film1.setDuration(0);
        Exception exc = assertThrows(ValidationException.class, () -> controller.postFilm(film1));
        assertEquals("продолжительность фильма должна быть положительной!", exc.getMessage());
    }

    @Test
    void getFilms() {
        assertEquals(controller.getAllFilms().size(), 0);

        controller.postFilm(film1);
        controller.postFilm(film2);
        assertEquals(controller.getAllFilms().size(), 2);
    }

    @Test
    void updateFilmValid() throws ValidationException {
        controller.postFilm(film1);
        film1.setDescription("Новое описание фильма 1");
        controller.putFilm(film1);
        assertEquals("Новое описание фильма 1", film1.getDescription());
    }

    @Test
    void updateFilmInValId() throws ValidationException {
        controller.postFilm(film1);
        film1.setId(33);
        assertNotEquals(1, film1.getId());
    }


}
