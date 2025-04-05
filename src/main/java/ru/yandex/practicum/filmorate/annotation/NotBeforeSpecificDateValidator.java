package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotBeforeSpecificDateValidator implements ConstraintValidator<NotBeforeSpecificDate, LocalDate> {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null || releaseDate.isBefore(MIN_DATE)) {
            return false;
        }
        return true;
    }
}

/*
Пробывал вот такие варианты но не помогает. Вот к примеру аннотация @PastOrPresent близкая по смыслу, и если мы даем данные
на обновления без этого поля в User. То корректно проходим по блоку обновления а не сваливаемся сразу как здесь с Film
с полем releaseDate

public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        try {
            return !releaseDate.isBefore(MIN_DATE);
        } catch (NullPointerException e) {
            return false;

 public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return Optional.ofNullable(releaseDate)
                .map(date -> !date.isBefore(MIN_DATE))
                .orElse(false);
    }

    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            releaseDate = LocalDate.MIN;
        }
        return !releaseDate.isBefore(MIN_DATE);
    }

    return !releaseDate.isBefore(MIN_DATE);
 */