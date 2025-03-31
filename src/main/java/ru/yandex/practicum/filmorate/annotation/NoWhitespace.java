package ru.yandex.practicum.filmorate.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoWhitespace {
    String message() default "Логин не может содержать пробелы";
}