package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = NoWhitespaceValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoWhitespace {
    String message() default "Логин не может содержать пробелы";

    Class<?>[] groups() default {}; //без этой и строки ниже валидатор работает не корректно

    Class<? extends Payload>[] payload() default {};
}
