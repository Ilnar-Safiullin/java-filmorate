package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = NotBeforeSpecificDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBeforeSpecificDate {
    String message() default "Дата релиза должна быть не раньше 28 декабря 1895 года";

    Class<?>[] groups() default {}; //без этой и строки ниже валидатор работает не корректно

    Class<? extends Payload>[] payload() default {};
}
