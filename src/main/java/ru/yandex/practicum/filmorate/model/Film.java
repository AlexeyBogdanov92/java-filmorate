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
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private long duration;
    private static int countID = 0;
    private final Set<Integer> likes = new HashSet<>();

    public void generateId() {
        countID++;
        this.id = countID;
    }

    public static void resetCountID() {
        countID = 0;
    }

}
