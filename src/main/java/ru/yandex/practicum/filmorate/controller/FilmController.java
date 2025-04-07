package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        return films.values();
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@RequestBody @Valid Film updatedFilm) {
        log.info("Обновление фильма с ID: {}", updatedFilm.getId());
        Film existingFilm = films.get(updatedFilm.getId());
        if (existingFilm == null) {
            log.warn("Ошибка обновления фильма ID {} не найден", updatedFilm.getId());
            throw new ValidationException("Фильм с таким айди не найден");
        }
        if (updatedFilm.getName() != null) {
            existingFilm.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null) {
            existingFilm.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() != null) {
            existingFilm.setDuration(updatedFilm.getDuration());
        }
        log.info("Фильм успешно обновлен: {}", existingFilm);
        return existingFilm;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Попытка добавления фильма: {}", film);
        if (films.containsKey(film.getId())) {
            log.warn("Ошибка добавления фильма: фильм с таким ID уже есть: {}", film.getId());
            throw new ValidationException("Фильм с таким ID уже есть");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }
}