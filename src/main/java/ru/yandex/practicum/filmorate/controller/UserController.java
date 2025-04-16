package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("{userId}/friends")
    public Collection<User> userFriends(@PathVariable Integer userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable Integer userId,
                                          @PathVariable Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public User update(@RequestBody @Valid User updatedUser) {
        return userService.updateUser(updatedUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Integer userId,
                          @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer userId,
                             @PathVariable Integer friendId) {
        userService.removeFriend(userId, friendId);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public User addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }
}