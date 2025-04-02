package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    private Integer getNextId() {
        return users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return users.values();
    }

    @Validated
    @PutMapping
    public User update(@RequestBody User updatedUser) {
        log.info("Обновление пользователя с ID: {}", updatedUser.getId());
        User existingUser = users.get(updatedUser.getId());
        if (existingUser == null) {
            log.warn("Ошибка обновления пользователя ID {} не найден", updatedUser.getId());
            throw new ValidationException("Пользователь с таким айди не найден");
        }
        if (updatedUser.getName().isBlank() || updatedUser.getName().isEmpty()) {
            log.info("У пользователя нет имени, будет использован Логин {}", updatedUser.getLogin());
            updatedUser.setName(updatedUser.getLogin());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank() && updatedUser.getEmail().contains("@")) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null && updatedUser.getLogin().contains(" ")) {
            existingUser.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getBirthday() != null && updatedUser.getBirthday().isBefore(LocalDate.of(1895, 12, 28))) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
        log.info("Пользователь успешно обновлен: {}", updatedUser);
        return updatedUser;
        /*
        я не писал log.info и оставил магическое число в LocalDate потомучто наверное не верно сделал
        Прям как валидировать внутри не понял из статьи и нейросетей. Может вернем как предыдущий коммит был? Симпатичнее было ато
         */
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Попытка добавления Пользователя: {}", user);
        if (users.containsKey(user.getId())) {
            log.warn("Ошибка добавления пользователя: пользователь с таким ID уже есть: {}", user.getId());
            throw new ValidationException("Пользователь с таким ID уже есть");
        }
        if (user.getName() == null) {
            log.info("У пользователя нет имени, будет использован Логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }
}
