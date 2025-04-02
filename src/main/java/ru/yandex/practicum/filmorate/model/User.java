package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.NoWhitespace;

import java.time.LocalDate;

@Data
public class User {

    private Integer id;

    @Email(message = "Электронная почта должна быть корректной")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NoWhitespace(message = "логин не может быть пустым и содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть из будущего")
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
/*
    public @Email(message = "Электронная почта должна быть корректной") @NotBlank(message = "Электронная почта не может быть пустой") String getEmail() {
        return email;
    }
    мне вот такие геттеры придется сделать наверное чтобы при обновлении валидировать эти поля из запроса
 */
}