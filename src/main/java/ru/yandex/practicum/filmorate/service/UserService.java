package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Попытка добавления в друзья у пользователя с ID: {}", userId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Добавлен друг у пользователя с ID: {}", userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        log.info("Попытка удаления друга у пользователя с ID: {}", userId);
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
        log.info("Удален друг у пользователя с ID: {}", userId);
    }

    public Set<User> getUserFriends(Integer userID) {
        User user = userStorage.getUserById(userID);
        return user.getFriends().stream()
                .map(userStorage::getUserById) // Преобразуем ID в объекты User
                .collect(Collectors.toSet()); // Собираем результат в Set
    }

    public Set<User> getCommonFriends(Integer userId1, Integer friendId) {
        log.info("Попытка получения общих друзей у пользователя с ID: {}", userId1);
        Set<Integer> friendsOfUser1 = userStorage.getUserById(userId1).getFriends();
        Set<Integer> friendsOfUser2 = userStorage.getUserById(friendId).getFriends();
        Set<Integer> commonFriends = new HashSet<>(friendsOfUser1);
        commonFriends.retainAll(friendsOfUser2);
        return commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}
