package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank(message = "name - не должно быть пустым")
    private String name;
    @Size(max = 200, message = "description - не должно привышать 200 символов")
    private String description;
    @NotNull(message = "releaseDate обязателен к заполнению")
    private LocalDate releaseDate;
    @Min(value = 1, message = "duration - должна быть не меньше 1 минуты")
    private long duration;
    private static int countID = 0;
    private final Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres;
    private MPA mpa;

    public void generateId() {
        countID++;
        this.id = countID;
    }

    public static void resetCountID() {
        countID = 0;
    }

}
