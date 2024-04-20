package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @Min(value = 1, message = "min 1")
    @Max(value = 6, message = "max 6")
    private int id;
    @NotBlank
    private String name;
}