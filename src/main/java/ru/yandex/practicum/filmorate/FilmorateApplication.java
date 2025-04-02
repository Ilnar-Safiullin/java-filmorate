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
Вот еще вариант наклепал. Но тоже страшный получается, через обычные if даже симпатичнее. Но технически наверное этот лучше
чем просто if
Может вернуть как было в предыдущим коммите?
 */