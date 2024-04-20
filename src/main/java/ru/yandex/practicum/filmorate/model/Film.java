package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotations.CorrectDate;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    @PositiveOrZero(message = "Id не может быть отрицательным")
    private Integer id;
    @NotBlank(message = "name - не должно быть пустым")
    private String name;
    @Size(min = 1, max = 200, message = "description - не должна привышать 200 символов")
    private String description;
    @CorrectDate(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Min(value = 1, message = "duration - должна быть не меньше 1 минуты")
    private long duration;
    private Set<Genre> genres;
    private MPA mpa;
    private Set<Integer> likes;
    private static int countID = 0;

    public void generateId() {
        countID++;
        this.id = countID;
    }

    public static void resetCountID() {
        countID = 0;
    }


}
