package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("{userId}/friends")
    public Collection<UserDto> userFriends(@PathVariable Integer userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<UserDto> commonFriends(@PathVariable Integer userId,
                                             @PathVariable Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public UserDto update(@RequestBody @Valid UserDto userDto) {
        return userService.updateUser(userDto);
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
    public UserDto addUser(@RequestBody @Valid UserDto userDtoRequest) {
        return userService.addUser(userDtoRequest);
    }
}