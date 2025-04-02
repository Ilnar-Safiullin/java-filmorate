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

public class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validationTest() {
        User user = new User(1, "emailmail.ru", "log in", "name", LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Электронная почта должна быть корректной",
                        "логин не может быть пустым и содержать пробелы",
                        "Дата рождения не может быть из будущего");
    }

    @Test
    void validationNotNulField() {
        User user = new User(1, null, "login", "name", null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Электронная почта не может быть пустой",
                        "Дата рождения не может быть пустой");
    }
}
/*
Почему то не доступны сеттеры. Пришлось через конструктор делать. Пытался делать так сперва
User user = new User();
user.setId(1); - но не дает вызвать ни один сеттер, просто красным горит. Курратор молчит как всегда. Пошел поэтому
через конструктор
Может они не работали потомучто у меня не было конструктора? Сейчас сделал конструктор и начали работать
 */