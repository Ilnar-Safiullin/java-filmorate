package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotBeforeSpecificDateValidator implements ConstraintValidator<NotBeforeSpecificDate, LocalDate> {
    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate == null || releaseDate.isAfter(minDate) || releaseDate.isEqual(minDate);
    }
}