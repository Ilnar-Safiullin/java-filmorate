package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {

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

    private Set<Integer> friends = new HashSet<>();

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
