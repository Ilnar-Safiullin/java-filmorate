package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}
}

/*
Сергей привет. Не смог победить поле releaseDate. Может это корректно что мы так оставим что при обновлении фильма
получается нам всегда придется в том числе подавать поле корректное releaseDate. Или убирать тогда свой валидатор
и просто через if обычные делать? Расписал в классах Film и NotBeforeSpecificDateValidator

Или же оставить аннотацию эту только для создания а при обновлении просто вручную тогда через if по старинке
 */