package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.UpdateGenreRequest;
import ru.yandex.practicum.filmorate.dto.mpa.NewMpaRequest;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreMapper {

    public Genre mapToGenre(NewMpaRequest request) {
        return new Genre(
                null,
                request.getName()
        );
    }

    public Genre updateFromRequest(Genre existing, UpdateGenreRequest request) {
        if (request.hasName()) existing.setName(request.getName());
        return existing;
    }

    public GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}
