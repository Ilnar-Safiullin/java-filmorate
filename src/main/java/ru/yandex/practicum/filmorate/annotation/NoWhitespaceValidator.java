package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return login != null && !login.trim().isEmpty() && !login.contains(" ");
    }
}