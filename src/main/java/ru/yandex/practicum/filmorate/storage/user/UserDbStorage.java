package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@AllArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {
    protected final JdbcTemplate jdbc;

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String GET_All_USERS = "SELECT u.user_id, u.email, u.login, u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id";
    private static final String GET_USER_BY_ID = "SELECT u.user_id, u.email, u.login, u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id " +
            "WHERE u.user_id = ?";


    @Override
    public User addUser(User user) {
        Integer generatedId = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(generatedId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbc.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public User getUserById(int id) {
        List<User> user = jdbc.query(GET_USER_BY_ID, new UserRowMapper(), id);
        if (user.isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return user.getFirst();
    }

    @Override
    public Collection<User> getAllUsers() {
        List<User> users = jdbc.query(GET_All_USERS, new UserRowMapper());
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователей пока нет");
        }
        return users;
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null) {
            return id.intValue();
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}