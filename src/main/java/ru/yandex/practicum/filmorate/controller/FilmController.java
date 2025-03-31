package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

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
    public Film update(@Valid @RequestBody Film updatedFilm) {
        log.info("Обновление фильма с ID: {}", updatedFilm.getId());
        Film existingFilm = films.get(updatedFilm.getId());
        if (existingFilm == null) {
            log.warn("Ошибка обновления фильма ID {} не найден", updatedFilm.getId());
            throw new ValidationException("Фильм с таким айди не найден");
        }
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
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
