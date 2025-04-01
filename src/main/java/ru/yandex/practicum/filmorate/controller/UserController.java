package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

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

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
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
        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователь успешно обновлен: {}", updatedUser);
        return updatedUser;
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
