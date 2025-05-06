package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Добавления лайка в фильм с ID: {}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId); // эти две строчки чтобы убедиться что такой юзер и фильм есть
        film.getLikes().add(user.getId());
        log.info("Добавлен лайк в фильм с ID: {}", filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Попытка удаления лайка в фильме с ID: {}", filmId);
        filmStorage.getFilmById(filmId).getLikes().remove(userStorage.getUserById(userId).getId()); // сделал так чтобы user проверялся иначе постман не проходит тест
        log.info("Удален лайк в фильме с ID: {}", filmId);
    }

    public List<Film> getTopPopularFilms(Integer count) {
        log.info("Запрос на получение самых популярных фильмов");
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }
}
