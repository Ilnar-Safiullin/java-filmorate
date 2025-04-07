package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
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
    @Validated(Marker.OnUpdate.class)
    public User update(@RequestBody @Valid User updatedUser) {
        log.info("Обновление пользователя с ID: {}", updatedUser.getId());
        User existingUser = users.get(updatedUser.getId());
        if (existingUser == null) {
            log.warn("Ошибка обновления: пользователь с ID {} не найден", updatedUser.getId());
            throw new ValidationException("Пользователь с таким ID не найден");
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null && !updatedUser.getLogin().isEmpty()) {
            existingUser.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getBirthday() != null) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
        log.info("Пользователь успешно обновлен: {}", existingUser);
        return existingUser;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public User addUser(@RequestBody @Valid User user) {
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