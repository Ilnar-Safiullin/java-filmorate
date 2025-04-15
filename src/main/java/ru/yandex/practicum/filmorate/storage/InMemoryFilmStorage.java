package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();
    private Integer currentId = 0;

    @Override
    public Film addFilm(Film film) {
        log.info("Попытка добавления фильма: {}", film);
        if (films.containsKey(film.getId())) {
            log.warn("Ошибка добавления фильма: фильм с таким ID уже есть: {}", film.getId());
            throw new ValidationException("Фильм с таким ID уже есть");
        }
        film.setId(++currentId);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма с ID: {}", updatedFilm.getId());
        Film existingFilm = films.get(updatedFilm.getId());
        if (existingFilm == null) {
            log.warn("Ошибка обновления фильма ID {} не найден", updatedFilm.getId());
            throw new NotFoundException("Фильм с таким айди не найден");
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
        log.info("Фильм успешно обновлен: {}", updatedFilm);
        films.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film getFilmById(int id) {
        log.info("Запрос на получение фильма по id: {}", id);
        if (films.containsKey(id)) {
            log.info("Фильм с данным id {} найден", id);
            return films.get(id);
        }
        log.warn("Фильм с данным id не найден: {}", id);
        throw new NotFoundException("Фильм с данным id не найден: " + id);
    }

    @Override
    public ArrayList<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
    }

    @Override
    public Map<Integer, Set<Integer>> getFilmLikes() {
        return filmLikes;
    }
}