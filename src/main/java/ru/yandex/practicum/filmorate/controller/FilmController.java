package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@Validated
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
    public FilmDto findById(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopPopularFilms(count);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public FilmDto update(@RequestBody @Valid FilmDto filmDtoRequest) {
        return filmService.updateFilm(filmDtoRequest);
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
    public FilmDto addFilm(@RequestBody @Valid FilmDto filmDtoRequest) {
        return filmService.addFilm(filmDtoRequest);
    }
}