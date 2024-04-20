package ru.yandex.practicum.filmorate.annotations;

import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilmValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectDate {
    String message() default "{CorrectDate}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}