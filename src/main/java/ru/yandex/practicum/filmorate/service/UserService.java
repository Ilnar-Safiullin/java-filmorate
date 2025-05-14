package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserDbStorage userDbStorage,
                       UserMapper userMapper) {
        this.userDbStorage = userDbStorage;
        this.userMapper = userMapper;
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Попытка добавления в друзья у пользователя с ID: {}", userId);
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        userDbStorage.addFriend(userId, friendId);
        log.info("Добавлен друг у пользователя с ID: {}", userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userDbStorage.getUserById(userId); //проверка что такой юзер существует
        User friend = userDbStorage.getUserById(friendId); //проверка что такой юзер существует
        userDbStorage.removeFriend(userId, friendId);
        log.info("Пользователь с ID {} перестал дружить с пользователем с ID {}", userId, friendId);
    }

    public List<UserDto> getUserFriends(int userId) {
        userDbStorage.getUserById(userId); //проверка что такой юзер существует
        List<User> friends = userDbStorage.getFriends(userId);
        return friends.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Integer userId1, Integer friendId) { // Здесь схитрил, ато не могу правильно запрос с таблицы нормально написать на общих друзей и сделал так
        log.info("Попытка получения общих друзей у пользователя с ID: {}", userId1);
        List<Integer> friendsOfUser1 = userDbStorage.getFriendsId(userId1);
        List<Integer> friendsOfUser2 = userDbStorage.getFriendsId(friendId);
        Set<Integer> commonFriends = new HashSet<>(friendsOfUser1);
        commonFriends.retainAll(friendsOfUser2);
        return commonFriends.stream()
                .map(userDbStorage::getUserById)
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(NewUserRequest request) {
        log.info("Попытка добавления нового пользователя");
        User user = userMapper.mapToUser(request);
        user = userDbStorage.addUser(user);
        log.info("Пользователь успешно создан: {}", user);
        return userMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.info("Попытка обновления пользователя");
        User updatedUser = userDbStorage.getUserById(request.getId());
        userMapper.updateFromRequest(updatedUser, request);
        updatedUser = userDbStorage.updateUser(updatedUser);
        log.info("Пользователь обновлен");
        return userMapper.mapToUserDto(updatedUser);
    }

    public UserDto getUserById(int id) {
        User user = userDbStorage.getUserById(id);
        return userMapper.mapToUserDto(user);
    }

    public Collection<UserDto> getAllUsers() {
        return userDbStorage.getAllUsers()
                .stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(int id) {
        userDbStorage.deleteUser(id);
    }
}
