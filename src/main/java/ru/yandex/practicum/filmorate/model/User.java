package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "email - не может быть пустым")
    @Email
    private String email;
    @NotBlank(message = "login - не может быть пустым")
    private String login;
    private String name;
    @NotNull(message = "birthday - не может быть пустым")
    @Past(message = "birthday - не может быть из будущего")
    private LocalDate birthday;
    private static int countID = 0;
    private final Set<Integer> friends = new HashSet<>();

    public void generateId() {
        countID++;
        this.id = countID;
    }

    public static void resetCountID() {
        countID = 0;
    }

}
