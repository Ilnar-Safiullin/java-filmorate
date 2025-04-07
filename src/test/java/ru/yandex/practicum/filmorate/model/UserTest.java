package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.annotation.Marker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validationCorrectUserTest() {
        User user = new User(1, "email@mail.ru", "login", "name", LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class);
        assertThat(violations).hasSize(0);
    }

    @Test
    void validationEmailTest() {
        User user = new User(1, "emailmail.ru", "login", "name", LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Электронная почта должна быть корректной");
    }

    @Test
    void validationEmailEmptyTest() {
        User user = new User(1, "", "login", "name", LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Электронная почта не может быть пустой");
    }

    @Test
    void validationLoginTest() {
        User user = new User(1, "mail@mail.ru", "log in", "name", LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Логин не должен содержать пробелы");
    }

    @Test
    void validationLoginEmptyTest() {
        User user = new User(1, "mail@mail.ru", "", "name", LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "логин не может быть пустым",
                        "Логин не должен содержать пробелы");
    }

    @Test
    void validationBirthdayTest() {
        User user = new User(1, "mail@mail.ru", "login", "name", LocalDate.now().plusDays(1));
        User user2 = new User(1, "mail@mail.ru", "login", "name", LocalDate.now().minusDays(1));
        User user3 = new User(1, "mail@mail.ru", "login", "name", LocalDate.now());
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user, Marker.OnCreate.class, Marker.OnUpdate.class));
        violations.addAll(validator.validate(user2, Marker.OnCreate.class, Marker.OnUpdate.class));
        violations.addAll(validator.validate(user3, Marker.OnCreate.class, Marker.OnUpdate.class));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата рождения не может быть из будущего");
    }

    @Test
    void birthdayNullTest() {
        User user = new User(1, "email@mail.ru", "login", "name", null);
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата рождения не может быть пустой");
    }

}
