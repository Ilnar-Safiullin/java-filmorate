package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return login != null && !login.contains(" ");
    }
}