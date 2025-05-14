package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final FilmMapper filmMapper;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmDbStorage filmDbStorage,
            FilmMapper filmMapper,
            UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.filmMapper = filmMapper;
        this.userDbStorage = userDbStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Добавления лайка в фильм с ID: {}", filmId);
        Film film = filmDbStorage.getFilmById(filmId);
        User user = userDbStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        filmDbStorage.addLikeFilm(filmId, userId);
        log.info("Добавлен лайк в фильм с ID: {}", filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Попытка удаления лайка в фильме с ID: {}", filmId);
        User user = userDbStorage.getUserById(userId);
        filmDbStorage.getFilmById(filmId).getLikes().remove(userId);
        filmDbStorage.deleteLikeFilm(filmId, userId);
        log.info("Удален лайк в фильме с ID: {}", filmId);
    }

    public List<FilmDto> getTopPopularFilms(Integer count) {
        log.info("Запрос на получение самых популярных фильмов");
        return filmDbStorage.get10PopularFilms().stream()
                .limit(count)
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(NewFilmRequest request) {
        log.info("Попытка добавления нового фильма");
        Film film = filmMapper.mapToFilm(request);
        film = filmDbStorage.addFilm(film);
        log.info("Фильм успешно создан: {}", film);
        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(int id, UpdateFilmRequest request) {
        log.info("Попытка обновления фильма");
        Film updatedFilm = filmDbStorage.getFilmById(id);
        filmMapper.updateFromRequest(updatedFilm, request);
        updatedFilm = filmDbStorage.updateFilm(updatedFilm);
        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public FilmDto getFilmById(int id) {
        Film film = filmDbStorage.getFilmById(id);
        return filmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getAllFilms() {
        return filmDbStorage.getAllFilms()
                .stream()
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

}
