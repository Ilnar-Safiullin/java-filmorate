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

    @PastOrPresent
    @NotNull
    private LocalDate birthday;
}