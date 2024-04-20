package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    //@Positive(message = "Id не может быть отрицательным")
    private Integer id;
    @NotBlank(message = "email - Не может быть пустым")
    @Email
    private String email;
    @NotBlank(message = "login - Не может быть пустым")
    @Pattern(regexp = "\\S*", message = "Пробел в логине.")
    private String login;
    private String name;
    @NotNull(message = "birthday - Не может быть пустым")
    @Past(message = "birthday - дата не должна быть будущим")
    private LocalDate birthday;
    private Set<Integer> friends;
    private static int countID = 0;

    public void generateId() {
        countID++;
        this.id = countID;
    }

    public static void resetCountID() {
        countID = 0;
    }

}
