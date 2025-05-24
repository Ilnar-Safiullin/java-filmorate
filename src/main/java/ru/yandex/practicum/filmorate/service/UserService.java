package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final UserMapper userMapper;
    private final FriendshipDbStorage friendshipDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserDbStorage userDbStorage,
                       UserMapper userMapper, FriendshipDbStorage friendshipDbStorage) {
        this.userDbStorage = userDbStorage;
        this.userMapper = userMapper;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Попытка добавления в друзья у пользователя с ID: {}", userId);
        User user = userDbStorage.getUserById(userId); //проверка что такой юзер существует
        User friend = userDbStorage.getUserById(friendId); //проверка что такой юзер существует
        user.getFriends().add(friendId);
        friendshipDbStorage.addFriend(userId, friendId);
        log.info("Добавлен друг у пользователя с ID: {}", userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userDbStorage.getUserById(userId); //проверка что такой юзер существует
        User friend = userDbStorage.getUserById(friendId); //проверка что такой юзер существует
        friendshipDbStorage.removeFriend(userId, friendId);
        log.info("Пользователь с ID {} перестал дружить с пользователем с ID {}", userId, friendId);
    }

    public List<UserDto> getUserFriends(int userId) {
        userDbStorage.getUserById(userId); //проверка что такой юзер существует
        Set<User> friends = friendshipDbStorage.getFriendsForUserId(userId);
        return friends.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Integer userId1, Integer friendId) { // Здесь схитрил, ато не могу правильно запрос с таблицы нормально написать на общих друзей и сделал так
        log.info("Попытка получения общих друзей у пользoвателя с ID: {}", userId1);
        Set<User> friendsOfUser1 = friendshipDbStorage.getFriendsForUserId(userId1);
        Set<User> friendsOfUser2 = friendshipDbStorage.getFriendsForUserId(friendId);
        friendsOfUser2.retainAll(friendsOfUser1);
        return friendsOfUser2.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDtoRequest) {
        log.info("Попытка добавления нового пользователя");
        User user = userMapper.mapToUser(userDtoRequest);
        user = userDbStorage.addUser(user);
        log.info("Пользователь успешно создан: {}", user);
        return userMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UserDto userDto) {
        log.info("Попытка обновления пользователя");
        User updatedUser = userDbStorage.getUserById(userDto.getId());
        userMapper.updateFromRequest(updatedUser, userDto);
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
}
