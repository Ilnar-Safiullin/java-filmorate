package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.NoWhitespace;

import java.time.LocalDate;

@Data
public class User {

    private Integer id;

    @Email(message = "Электронная почта должна быть корректной") // Почему не работает эта аннотация? он добавляет пользователя если даже не указан <@> в поле email
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NotEmpty(message = "Логин не может быть пустым")
    @NoWhitespace
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем") // То же не работает почему то, добавляет пользователя с полем birthday из будущего
    @NotNull(message = "Дата рождения обязательна")
    private LocalDate birthday;
}