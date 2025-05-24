package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserMapper {

    public User mapToUser(UserDto userDtoRequest) {
        return new User(
                null,
                userDtoRequest.getEmail(),
                userDtoRequest.getLogin(),
                userDtoRequest.getName(),
                userDtoRequest.getBirthday()
        );
    }

    public UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());
        return dto;
    }

    public User updateFromRequest(User existing, UserDto userDto) {
        if (userDto.hasEmail()) {
            existing.setEmail(userDto.getEmail());
        }
        if (userDto.hasLogin()) {
            existing.setLogin(userDto.getLogin());
        }
        if (userDto.hasName()) {
            existing.setName(userDto.getName());
        }
        if (userDto.hasBirthday()) {
            existing.setBirthday(userDto.getBirthday());
        }
        return existing;
    }
}