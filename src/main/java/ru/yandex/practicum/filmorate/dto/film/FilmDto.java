package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.annotation.NotBeforeSpecificDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;

    @NotBlank(message = "Название не может быть пустым", groups = Marker.OnCreate.class)
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;

    @NotBeforeSpecificDate(message = "дата релиза — не раньше 28 декабря 1895 года", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotNull(message = "Дата релиза обязательна", groups = Marker.OnCreate.class)
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Integer duration;

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();

    private Set<Integer> likes = new HashSet<>();


    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasMpaId() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }
}
