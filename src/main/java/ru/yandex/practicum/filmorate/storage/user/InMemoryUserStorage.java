package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer currentId = 0;

    @Override
    public User addUser(User user) {
        log.info("Попытка добавления Пользователя: {}", user);
        if (users.containsKey(user.getId())) {
            log.warn("Ошибка добавления пользователя: пользователь с таким ID уже есть: {}", user.getId());
            throw new ValidationException("Пользователь с таким ID уже есть");
        }
        if (user.getName() == null) {
            log.info("У пользователя нет имени, будет использован Логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(++currentId);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя с ID: {}", updatedUser.getId());
        User existingUser = users.get(updatedUser.getId());
        if (existingUser == null) {
            log.warn("Ошибка обновления: пользователь с ID {} не найден", updatedUser.getId());
            throw new NotFoundException("Пользователь с таким ID не найден");
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

    @Override
    public User getUserById(int id) {
        log.info("Попытка получить пользователя с ID: {}", id);
        if (users.containsKey(id)) {
            log.info("Найден пользователь с ID: {}", id);
            return users.get(id);
        }
        log.warn("Не найден пользователь с указанным ID: {}", id);
        throw new NotFoundException("Не найден пользователь с указанным ID");
    }

    @Override
    public ArrayList<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }
}