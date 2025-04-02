package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validationTest() {
        String longString = "a".repeat(201);
        Film film = new Film(1, "", longString, LocalDate.of(1895, 12, 27), -1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(4);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Название не может быть пустым",
                        "Максимальная длина описания — 200 символов",
                        "дата релиза — не раньше 28 декабря 1895 года и не должна быть 0",
                        "Продолжительность фильма должна быть положительным числом");
    }
}
