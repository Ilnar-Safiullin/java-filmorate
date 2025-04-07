package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.annotation.NotBeforeSpecificDate;

import java.time.LocalDate;

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

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
/*
Пробывал даже конструктор такой делать, чтобы он не рубил сразу при обновлении, чтобы не было ошибки нуллпоинт и он шел
по строчкам if. Но если на обновление не отправить корректную строку releaseDate, то он сразу выкидывает что
дата релиза — не раньше 28 декабря 1895 года, и мы даже по if строчкам как в методе обновления User не проходим
то есть при обновлении фильма я не могу отправить две строчки: id и name как в User. Мне обновлять тогда фильм без своей аннотации?
через страшные if получается. Не могу найти  @PastOrPresent код. Он близкий по смыслу и корректно работает. Пропускает
тело для обновления без поля birthday. А в Film так не работает

public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (releaseDate == null) {
            this.releaseDate = LocalDate.MIN;
        } else {
            this.releaseDate = releaseDate;
        }
        this.duration = duration;
    }
 */