package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {
    protected final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
             "WHERE user_id = ?";
    private static final String ADD_FRIEND_ID_QUERY = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_ID_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIEND_ID_QUERY =
            "SELECT friend_id FROM friendship WHERE user_id = ?";
    private static final String GET_COMMON_FRIEND_QUERY =
            "SELECT u.* FROM users AS u JOIN friendship AS f ON u.id = f.friend_id WHERE f.user_id = ? " +
                    "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                    "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

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
        try {
            User user = jdbc.queryForObject(FIND_BY_ID_QUERY, new Object[]{id}, new UserRowMapper());
            if (user == null) {
                throw new NotFoundException("Пользователь c таким id не найден: " + id);
            }

            // Получаем список друзей
            List<Integer> friendsList = jdbc.queryForList(GET_FRIEND_ID_QUERY, new Object[]{id}, Integer.class);
            user.setFriends(new HashSet<>(friendsList)); // Устанавливаем список друзей

            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь c таким id не найден: " + id);
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbc.query(FIND_ALL_QUERY, new UserRowMapper());
    }

    @Override
    public void deleteUser(int id) {
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

    public void addFriend(int userId, int friendId) {
        jdbc.update(ADD_FRIEND_ID_QUERY, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        jdbc.update(REMOVE_FRIEND_ID_QUERY, userId, friendId);
    }

    public List<Integer> getFriendsId(int userId) {
        List<Integer> friendsList = jdbc.queryForList(GET_FRIEND_ID_QUERY, new Object[]{userId}, Integer.class);
        return friendsList;
    }

    public List<User> getFriends(int userId) {
        List<Integer> friendsList = jdbc.queryForList(GET_FRIEND_ID_QUERY, new Object[]{userId}, Integer.class);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsList) {
            friends.add(getUserById(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return jdbc.query(
                GET_COMMON_FRIEND_QUERY,
                new BeanPropertyRowMapper<>(User.class),
                userId, otherId
        );
    }
}
