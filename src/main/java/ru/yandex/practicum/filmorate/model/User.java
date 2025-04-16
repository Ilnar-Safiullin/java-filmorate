package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;

    @Email(message = "Электронная почта должна быть корректной", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(message = "Электронная почта не может быть пустой", groups = Marker.OnCreate.class)
    private String email;

    @NotBlank(message = "логин не может быть пустым", groups = Marker.OnCreate.class)
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть из будущего", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotNull(message = "Дата рождения не может быть пустой", groups = Marker.OnCreate.class)
    private LocalDate birthday;

    private Set<Integer> friends;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}