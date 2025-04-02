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
Спасибо большое Сергей еще раз было очень интересно, прям получил удовольствие от тестов.
Почему то только недоступны сеттеры когда в тестах пытался через сеттер назначить значения полей film и user.
Вернее когда не было конструктора были не доступны. А теперь с конструктором доступны стали.
Не смог валидировать через аннотации внутри в методе PUT (обновлений) валидировал просто через if
наверное это неправильно и выглядит ужасно поэтому второй класс FilmController не трогал пока.
Может вернуть как было в предыдущим коммите?
 */