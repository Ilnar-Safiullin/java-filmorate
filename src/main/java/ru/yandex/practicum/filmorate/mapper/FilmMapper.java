package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Component
public class FilmMapper {
    private static final int GENRE_MIN = 1;
    private static final int GENRE_MAX = 6;
    private static final int MPA_MIN = 1;
    private static final int MPA_MAX = 6;

    public Film mapToFilm(FilmDto filmDtoRequest) {
        if (filmDtoRequest.getMpa().getId() > MPA_MAX || filmDtoRequest.getMpa().getId() < MPA_MIN) {
            throw new NotFoundException("Не верный id Mpa");
        }
        Set<Genre> genres = filmDtoRequest.getGenres();
        for (Genre genre : genres) {
            if (genre.getId() > GENRE_MAX || genre.getId() < GENRE_MIN) {
                throw new NotFoundException("Не верный id Genre");
            }
        }
        return new Film(
                null,
                filmDtoRequest.getName(),
                filmDtoRequest.getDescription(),
                filmDtoRequest.getReleaseDate(),
                filmDtoRequest.getDuration(),
                filmDtoRequest.getGenres(),
                filmDtoRequest.getMpa()
        );
    }

    public Film updateFromRequest(Film existing, FilmDto filmDtoRequest) {
        if (filmDtoRequest.hasName()) existing.setName(filmDtoRequest.getName());
        if (filmDtoRequest.hasDescription()) existing.setDescription(filmDtoRequest.getDescription());
        if (filmDtoRequest.hasReleaseDate()) existing.setReleaseDate(filmDtoRequest.getReleaseDate());
        if (filmDtoRequest.hasDuration()) existing.setDuration(filmDtoRequest.getDuration());
        if (filmDtoRequest.hasMpaId()) existing.setMpa(filmDtoRequest.getMpa());
        if (filmDtoRequest.hasGenres()) existing.setGenres(filmDtoRequest.getGenres());
        return existing;
    }

    public FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(film.getMpa());
        dto.setGenres(film.getGenres());
       // dto.setLikes(film.getLikes());
        return dto;
    }
}