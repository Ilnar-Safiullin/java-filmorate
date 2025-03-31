package ru.yandex.practicum.filmorate.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBeforeSpecificDate {
    String message() default "Дата релиза должна быть не раньше 28 декабря 1895 года";
}