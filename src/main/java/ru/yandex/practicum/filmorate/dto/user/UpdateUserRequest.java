package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private int id;

    @Email(message = "Электронная почта должна быть корректной")
    private String email;

    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasLogin() {
        return login != null && !login.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
