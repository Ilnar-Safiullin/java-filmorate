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
Сергей Спасибо большое) все вроде получилось как надо)
Я сперва подумал что нам может приходить пользователь со всеми полями,просто какието поля у него будут не корректные,
и мы должны будем заменить только корректные.
Но оказывается может придти только 2 поля к примеру. Пример: первое обязательное id и name. И тогда мы должны провалидировать
поле name. И все получилось =)
Хороших тебе выходных)
 */