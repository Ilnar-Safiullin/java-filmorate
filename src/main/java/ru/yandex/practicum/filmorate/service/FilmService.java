package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    //Хранение лайков я кинул в InMemoryFilmStorage, или правильнее в этом классе было оставить?

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Добавления лайка в фильм с ID: {}", filmId);
        if (filmStorage.getFilmById(filmId) != null && userStorage.getUserById(userId) != null) {
            filmStorage.getFilmLikes().computeIfAbsent(filmId, id -> new HashSet<>()).add(userId);
            log.info("Добавлен лайк в фильм с ID: {}", filmId);
            return;
        }
        log.warn("Фильм с таким ID не найден: {}", filmId);
        throw new NotFoundException("Фильм с таким айди не найден");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Удаление лайка в фильме с ID: {}", filmId);
        Set<Integer> likes = filmStorage.getFilmLikes().get(filmId);
        if (likes != null && userStorage.getUserById(userId) != null) {
            likes.remove(userId);
            log.info("Удален лайк в фильме с ID: {}", filmId);
            return;
        }
        log.warn("Фильм или пользователь с таким ID не найден: {}", filmId);
        throw new NotFoundException("Фильм или пользователь с таким айди не найден");
    }

    public ArrayList<Film> getTopPopularFilms(Integer count) {
        log.info("Запрос на получение самых популярных фильмов");
        if (filmStorage.getFilmLikes().isEmpty()) {
            log.warn("Список популярных фильмов пуст");
            throw new NotFoundException("Список популярных фильмов пуст");
        }
        ArrayList<Map.Entry<Integer, Set<Integer>>> sortedFilms = new ArrayList<>(filmStorage.getFilmLikes().entrySet());
        sortedFilms.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));
        ArrayList<Film> topFilms = new ArrayList<>();
        for (int i = 0; i < Math.min(count, sortedFilms.size()); i++) {
            Integer filmId = sortedFilms.get(i).getKey();
            topFilms.add(filmStorage.getFilmById(filmId));
        }
        log.info("Самые популярные фильмы получены");
        return topFilms;
    }
}
