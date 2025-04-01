package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotBeforeSpecificDateValidator implements ConstraintValidator<NotBeforeSpecificDate, LocalDate> {
    private final LocalDate minDate = LocalDate.of(1895, 12, 28); //чек стайл не пропускает MIN_DATE

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate != null && !releaseDate.isBefore(minDate);
    }
}