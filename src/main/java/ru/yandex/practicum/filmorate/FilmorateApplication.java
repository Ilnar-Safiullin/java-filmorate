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
Сергей привет вроде все сделал, тесты только не получилось запустить, у нас в текущей теме написано что мы тесты эти будем
проходить на след спринте вроде как и там сказали сделать он тест простой (который единственный работает у меня) пока я запушил,
параллельно буду искать как их запустить. В группе тоже написал пока не ответили, отправляю так как у нас же сроки там горят.
Спасибо и Хорошего тебе Дня!

upd. Смог запустить тесты, не в той папки было. Но общий тест где все проходит без ошибок. Genre как будто не видит таблицу вообще
Like сторадж тоже валится не пойму почему. Постман приэтом все норм, и общий тесты все норм
 */