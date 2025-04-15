package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday, friends);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}