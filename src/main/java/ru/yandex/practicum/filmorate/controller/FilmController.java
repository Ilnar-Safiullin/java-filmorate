package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDto findById(@PathVariable("postId") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopPopularFilms(count);
    }

    @PutMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public FilmDto update(
            @PathVariable @Positive(message = "ID фильма должен быть положительным") int id,
            @RequestBody @Valid UpdateFilmRequest request) {
        return filmService.updateFilm(id, request);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Integer filmId,
                        @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable Integer filmId,
                           @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public FilmDto addFilm(@RequestBody @Valid NewFilmRequest request) {
        return filmService.addFilm(request);
    }
}