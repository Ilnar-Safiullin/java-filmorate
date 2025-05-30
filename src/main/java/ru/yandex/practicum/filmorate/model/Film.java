package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.annotation.NotBeforeSpecificDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

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

    private Set<Integer> likes;

    private Set<Genre> genres = new HashSet<>();

    @NotNull(message = "Рейтинг Мпа обязателен", groups = Marker.OnCreate.class)
    private Mpa mpa;

    public Film() {
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Set<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }
}