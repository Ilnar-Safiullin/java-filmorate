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
Сергей привет это был самый тяжелый спринт, не высыпался всю неделю чтобы это сделать, местами я схитрил, местами спросил
у других, очень долго все собирал. Ужс как ты будешь разбираться с моим кодом, извини заранее если кривой и работы тебе
добавляю. Хороших тебе выходных ! upd. Очень сильно обновил проект по сравнению с первым пушим, сказали в группе нельзя
в циклах обращаться к базе, и один сторадж не должен притягивать другой сторадж, перенес в сервис работу со стораджами
 */