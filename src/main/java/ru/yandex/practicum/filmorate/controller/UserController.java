package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final Validator validator;

    public UserController(Validator validator) {
        this.validator = validator;
    }

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
        if (updatedUser.getLogin() != null) {
            Set<ConstraintViolation<User>> violations = validator.validateProperty(updatedUser, "login");
            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
                    log.warn("Ошибка валидации логина: {}", violation.getMessage());
                }
            } else {
                existingUser.setLogin(updatedUser.getLogin());
            }
        }
        if (updatedUser.getEmail() != null) {
            Set<ConstraintViolation<User>> violations = validator.validateProperty(updatedUser, "email");
            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
                    log.warn("Ошибка валидации электронной почты: {}", violation.getMessage());
                }
            } else {
                existingUser.setEmail(updatedUser.getEmail());
            }
        }
        if (updatedUser.getBirthday() != null) {
            Set<ConstraintViolation<User>> violations = validator.validateProperty(updatedUser, "birthday");
            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
                    log.warn("Ошибка валидации даты рождения: {}", violation.getMessage());
                }
            } else {
                existingUser.setBirthday(updatedUser.getBirthday());
            }
        }
        log.info("Пользователь успешно обновлен: {}", updatedUser);
        return existingUser;
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

